package io.github.virtualstocksim.scraper;


import com.google.gson.*;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.stock.StockDatabase;
import io.github.virtualstocksim.stock.stockrequest.StockResponseCode;
import io.github.virtualstocksim.util.Errorable;
import io.github.virtualstocksim.util.json.JsonError;
import io.github.virtualstocksim.util.json.JsonUtil;
import io.github.virtualstocksim.util.priority.PriorityCallable;
import io.github.virtualstocksim.util.priority.Priority;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.util.IO;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class Scraper {

    private static final ThreadPoolExecutor executor = new ScraperTaskExecutor();

    private static final Logger logger = LoggerFactory.getLogger(Scraper.class);

    public static void shutdown()
    {
        executor.shutdownNow();
    }

    /**
     * Get the publicly available description of a company for the given stock symbol
     * @param symbol Stock symbol of company
     * @param priority Execution priority of the request
     * @return Description of company as string or error code
     */
    public static Errorable<String, StockResponseCode> getDescription(String symbol, Priority priority)
    {
        if(symbolInvalid(symbol))
        {
            return Errorable.WithError(StockResponseCode.INVALID_SYMBOL);
        }

        logger.info("Submitting company description request for '" + symbol + "'");
        String url = "https://finance.yahoo.com/quote/" + symbol + "/profile";
        Optional<Connection.Response> response = getJsoupResponse(priority, url);

        if(!response.isPresent())
            return Errorable.WithError(StockResponseCode.SERVER_ERROR);

        try
        {
            // Attempt to get company description from page
            Document doc = response.get().parse();
            Element elem = doc.getElementsByClass("Mt(15px) Lh(1.6)").first();
            String description;

            // If elements containing description where found and the description isn't empty
            if(elem != null && (description = elem.text()).trim().length() > 0)
            {
                return Errorable.WithValue(description);
            }
            // Otherwise fallback to quote homepage for stock and try again
                // This page contains the description as well but has much more content that has to be
                // retrieved and parsed so the first link is ideal
            else
            {
                url = "https://finance.yahoo.com/quote/" + symbol;
                logger.warn("Was unable to get description for " + symbol + ". Falling back to " + url);
                // Task has already waited for first response to occur, so try to get next response immediately unless the task isn't vital
                response = getJsoupResponse(priority != Priority.LOW ? Priority.URGENT : priority, url);

                if(!response.isPresent())
                    return Errorable.WithError(StockResponseCode.SERVER_ERROR);

                logger.info("Parsing response");
                doc = response.get().parse();
                elem = doc.getElementsByClass("businessSummary").first();

                if(elem != null && !(description = elem.text()).isEmpty())
                {
                    return Errorable.WithValue(description);
                }
                else
                {
                    return Errorable.WithError(StockResponseCode.SERVER_ERROR);
                }
            }
        }
        catch (IOException e)
        {
            logger.error("Unable to parse returned document; URL: " + url + "\n", e);
            return Errorable.WithError(StockResponseCode.SERVER_ERROR);
        }
    }

    /**
     * Check if the stock exists online
     * @param symbol Stock to search for
     * @param priority Execution priority of the request
     * @return If the stock exists or error code
     */
    public static Errorable<Boolean, StockResponseCode> checkStockExists(String symbol, Priority priority)
    {
        if(symbolInvalid(symbol))
        {
            return Errorable.WithError(StockResponseCode.INVALID_SYMBOL);
        }

        logger.info("Submitting existence check for '" + symbol + "'");

        try
        {
            Errorable<Boolean, Integer> result = submit(new PriorityCallable<Errorable<Boolean, Integer>>(priority)
            {
                @Override
                public Errorable<Boolean, Integer> call() throws Exception
                {
                    String url = "https://finance.yahoo.com/quote/" + symbol;
                    logger.info("Checking if stock exists: " + url);
                    HttpGet request = new HttpGet(url);
                    try(
                            CloseableHttpClient client = HttpClients.createDefault();
                            CloseableHttpResponse response = client.execute(request)
                    )
                    {
                        StatusLine sl = response.getStatusLine();
                        int statusCode = sl.getStatusCode();
                        logger.trace("Response to: " + url + "\n" +
                                             "Status Code: " + statusCode + "\n" +
                                             "Status Message: " + sl.getReasonPhrase() + "\n" +
                                             "Content Type: " + response.getFirstHeader("Content-type").getValue() + "\n"
                                    );

                        if(statusCode == HttpStatus.SC_OK)
                        {
                            return Errorable.WithValue(true);
                        }
                        else if(statusCode == HttpStatus.SC_NOT_FOUND)
                        {
                            return Errorable.WithValue(false);
                        }
                        else
                        {
                            return Errorable.WithError(statusCode);
                        }
                    }
                }
            });

            if(result == null || result.isError())
            {
                return Errorable.WithError(StockResponseCode.SERVER_ERROR);
            }
            else
            {
                return Errorable.WithValue(result.getValue());
            }
        }
        catch (ExecutionException e)
        {
            Throwable cause = e.getCause();
            if(cause instanceof ClientProtocolException)
            {
                logger.error("\n", e);
            }
            else if(cause instanceof IOException)
            {
                logger.error("\n", e);
            }
            return Errorable.WithError(StockResponseCode.SERVER_ERROR);
        }
    }

    /**
     * Get the description and full price history for a stock
     * @param symbol Symbol of stock
     * @param timeInterval Time period interval in price history
     * @param priority Execution priority of the request
     * @return JsonObject containing company description and price history or error
     *          {
     *              "symbol": "",
     *              "description": "",
     *              "history": [
     *                              {
     *                                  "date": "",
     *                                  "open": "",
     *                                  "high": "",
     *                                  "low": "",
     *                                  "close": "",
     *                                  "adjclose": "",
     *                                  "volume": ""
     *                              }
     *                          ]
     *          }
     */
    public static Errorable<JsonObject, StockResponseCode> getDescriptionAndHistory(String symbol, TimeInterval timeInterval, Priority priority)
    {
        if(symbolInvalid(symbol))
        {
            return Errorable.WithError(StockResponseCode.INVALID_SYMBOL);
        }

        // Get the description
        Errorable<String, StockResponseCode> descriptionResp = getDescription(symbol, priority);
        if(descriptionResp.isError())
            return Errorable.WithError(descriptionResp.getError());

        logger.info("Submitting price history request for '" + symbol + "'");

        // Get the price history CSV file as a string
        String url = "https://query1.finance.yahoo.com/v7/finance/download/" + symbol +
                        "?period1=0&period2=" + Instant.now().toEpochMilli() +
                        "&interval=" + timeInterval.getPeriod()+"&events=history";
        Optional<String> response = getHttpBody(priority, url);

        if(!response.isPresent())
        {
            logger.error("Error while getting price history for '" + symbol + "'");
            return Errorable.WithError(StockResponseCode.SERVER_ERROR);
        }

        String csv = response.get();
        // Make sure the response is present and the body isn't empty
        if(csv.isEmpty())
            return Errorable.WithError(StockResponseCode.SERVER_ERROR);

        List<String> csvRows = new LinkedList<>(Arrays.asList(csv.split("\\n")));
        csvRows.remove(0);

        List<String> values = new ArrayList<>();

        for(String row : csvRows)
        {
            values.addAll(Arrays.asList(row.split(",")));
        }

        JsonArray priceHistory = new JsonArray();
        for (int i = 0; i < values.size() - 6; i += 7) {
            String[] properties = new String[7];
            // Fill properties array with time period property values
            //  and make sure that none of the properties are null.
            //  Yahoo Finance returns null for the time period if the time period
            //  is too new and there isn't data available yet.
            //      i.e. With a time interval of one month and the update is being run
            //          on the first of the month before the market opens
            boolean periodIsNull = false;
            for(int j = 0; j < 7 && !periodIsNull; ++j)
            {
                properties[j] = values.get(i+j);
                if(properties[j].equals("null"))
                {
                    periodIsNull = true;
                }
            }

            if(!periodIsNull)
            {
                JsonObject period = new JsonObject();
                period.addProperty("date",      properties[0]);
                period.addProperty("open",      properties[1]);
                period.addProperty("high",      properties[2]);
                period.addProperty("low",       properties[3]);
                period.addProperty("close",     properties[4]);
                period.addProperty("adjclose",  properties[5]);
                period.addProperty("volume",    properties[6]);
                priceHistory.add(period);
            }
        }

        JsonObject result = new JsonObject();
        result.addProperty("symbol", symbol);
        result.addProperty("description", descriptionResp.getValue());
        result.add("history", priceHistory);

        return Errorable.WithValue(result);
    }

    public static Collection<Errorable<Stock, StockResponseCode>> getCurrentData(Set<String> symbols, Priority priority) throws IOException
    {
        if(symbols.isEmpty())
            throw new IllegalArgumentException("Symbol list can't be empty");

        Map<String, Errorable<Stock, StockResponseCode>> resultMap = new HashMap<>();
        final List<String> validSymbols = new LinkedList<>();
        for(String symbol : symbols)
        {
            if(!symbolInvalid(symbol))
            {
                validSymbols.add(symbol);
            }
            else
            {
                // Preemptively add every other stock as a server error in-case one occurs the list can just be returned
                resultMap.put(symbol, Errorable.WithError(StockResponseCode.SERVER_ERROR));
            }
        }

        String url = "https://query2.finance.yahoo.com/v7/finance/quote?formatted=false&lang=en-US&region=US&symbols=" +
                String.join(",", validSymbols) +
                "&fields=symbol,regularMarketChange,regularMarketVolume,regularMarketPrice,regularMarketOpen";
        Optional<String> response = getHttpBody(priority, url);

        if(!response.isPresent())
            throw new IOException("Bad response");

        try
        {
            Errorable<JsonObject, JsonError> responseErrorable = JsonUtil.getAs(JsonParser.parseString(response.get()), JsonElement::getAsJsonObject);
            if(responseErrorable.isError())
                throw new IOException("Exception while getting response as JsonObject: " + responseErrorable.getError());

            JsonObject responseObj = responseErrorable.getValue();

            Errorable<JsonObject, JsonError> quoteResponseErrorable = JsonUtil.getMemberAs(responseObj, "quoteResponse", JsonElement::getAsJsonObject);
            if(quoteResponseErrorable.isError())
            {
                throw new IOException("Error getting current data 'quoteResponse' parent member");
            }

            JsonElement error = quoteResponseErrorable.getValue().get("error");
            if(error != null && !error.isJsonNull())
            {
                Errorable<JsonPrimitive, JsonError> errorErrorable = JsonUtil.getAs(error, JsonElement::getAsJsonPrimitive);
                if(errorErrorable.isError())
                {
                   throw new IOException("Error getting error returned by site " + errorErrorable.getError());
                }
                else
                {
                    throw new IOException("Site returned error: " + errorErrorable.getValue());
                }
            }

            Errorable<JsonArray, JsonError> resultErrorable = JsonUtil.getMemberAs(quoteResponseErrorable.getValue(), "result", JsonElement::getAsJsonArray);
            if(resultErrorable.isError())
            {
                throw new IOException("Error getting current data result array");
            }

            for(JsonElement element : resultErrorable.getValue())
            {
                logger.info("Quote Element: " + element);
                Errorable<JsonObject, JsonError> quoteErrorable = JsonUtil.getAs(element, JsonElement::getAsJsonObject);
                if(quoteErrorable.isError())
                {
                    logger.error("Error getting element as JsonObject: " + quoteErrorable.getError() + "\n");
                }
                else
                {
                    JsonObject quote = quoteErrorable.getValue();

                    String symbol = null;
                    BigDecimal currPrice = null;
                    BigDecimal previousClose = null;
                    int currVolume = -1;

                    Errorable<String, JsonError> symbolErrorable = JsonUtil.getMemberAs(quote, "symbol", JsonElement::getAsString);
                    if(symbolErrorable.isError() || symbolErrorable.getValue().isEmpty())
                    {
                        logger.error("Couldn't retrieve symbol: " + symbolErrorable.getError());
                    }
                    else
                    {
                        symbol = symbolErrorable.getValue();
                    }

                    // Current market price
                    Errorable<BigDecimal, JsonError> currPriceErrorable = JsonUtil.getMemberAs(quote, "regularMarketPrice", JsonElement::getAsBigDecimal);
                    if(currPriceErrorable.isError())
                    {
                        logger.error("Couldn't retrieve current market price: " + currPriceErrorable.getError());
                    }
                    else
                    {
                        currPrice = currPriceErrorable.getValue();
                    }

                    // Price at market open/previous close
                    Errorable<BigDecimal, JsonError> prevCloseErrorable = JsonUtil.getMemberAs(quote, "regularMarketPreviousClose", JsonElement::getAsBigDecimal);
                    if(prevCloseErrorable.isError())
                    {
                        logger.error("Couldn't retrieve previous market price: " + prevCloseErrorable.getError());
                    }
                    else
                    {
                        previousClose = prevCloseErrorable.getValue();
                    }

                    // Current volume
                    Errorable<Integer, JsonError> currVolumeErrorable = JsonUtil.getMemberAs(quote, "regularMarketVolume", JsonElement::getAsInt);
                    if(currVolumeErrorable.isError())
                    {
                        logger.error("Could not retrieve current market volume: " + currVolumeErrorable.getError());
                    }
                    else
                    {
                        currVolume = currVolumeErrorable.getValue();
                    }

                    if(symbol != null && currPrice != null && previousClose != null && currVolume > -1)
                    {
                        resultMap.put(symbol, Errorable.WithValue(new Stock(symbol, currPrice, previousClose, currVolume, null)));
                    }
                }
            }

        }
        catch (JsonParseException e)
        {
            throw new IOException("Exception while parsing current data response\n", e);
        }

        return resultMap.values();
    }

    private static boolean symbolInvalid(String symbol)
    {
        return symbol.contains("^") || symbol.length() > StockDatabase.getMaxSymbolLen();
    }

    /**
     * Submit request and get response
     * @param priority Execution priority of the connection request
     * @param url Url of the target
     * @return Response from the url
     */
    private static Optional<Connection.Response> getJsoupResponse(Priority priority, String url)
    {
        try
        {
            Connection.Response response = submit(new PriorityCallable<Connection.Response>(priority)
            {
                @Override
                public Connection.Response call() throws Exception
                {
                    logger.info("Executing GET request with Jsoup; Url: " + url);
                    Connection.Response r = Jsoup.connect(url).followRedirects(true).execute();
                    logger.trace("Response to: " + url + "\n" +
                                         "Status Code: " + r.statusCode() + "\n" +
                                         "Status Message: " + r.statusMessage() + "\n" +
                                         "Charset: " + r.charset() + "\n" +
                                         "Content Type: " + r.contentType() + "\n" +
                                         "Body: \n" + r.body() + "\n"
                                );
                    return r;
                }
            });

            return response == null ? Optional.empty() : Optional.of(response);
        }
        catch (ExecutionException e)
        {
            Throwable cause = e.getCause();
            if(cause instanceof MalformedURLException)
            {
                logger.error("Bad URL: " + url + "\n", e);
            }
            else if(cause instanceof HttpStatusException)
            {
                logger.error("Response for URL was '" + ((HttpStatusException) cause).getStatusCode() +"' not '200 OK'; URL: " + url + "\n", e);
            }
            else if(cause instanceof UnsupportedMimeTypeException)
            {
                logger.error("Response's MIME type is unsupported by JSOUP; URL: " + url + "\n", e);
            }
            else if(cause instanceof SocketTimeoutException)
            {
                logger.error("Connection request timed out; URL: " + url + "\n", e);
            }
            else if(cause instanceof IOException)
            {
                logger.error("Exception executing request; URL: " + url + "\n", e);
            }
            else
            {
                logger.error("Exception executing request; Priority: " + priority.asInt() + " URL: " + url + "\n", e);
            }
            return Optional.empty();
        }
    }

    private static Optional<String> getHttpBody(Priority priority, String url)
    {
        try
        {
            // Submit the task
            Errorable<String, Integer> response = submit(new PriorityCallable<Errorable<String, Integer>>(priority)
            {
                @Override
                public Errorable<String, Integer> call() throws Exception
                {
                    logger.info("Executing GET request with HttpClient; Url: " + url);

                    // Setup and execute the request
                    HttpGet request = new HttpGet(url);
                    try(
                            CloseableHttpClient client = HttpClients.createDefault();
                            CloseableHttpResponse response = client.execute(request)
                    )
                    {
                        // Attempt to get the response body
                        HttpEntity entity;
                        String body = null;
                        if((entity = response.getEntity()) != null)
                        {
                            body = EntityUtils.toString(entity);
                        }

                        // Log the response
                        StatusLine sl = response.getStatusLine();
                        int statusCode = sl.getStatusCode();
                        logger.trace("Response to: " + url + "\n" +
                                             "Status Code: " + statusCode + "\n" +
                                             "Status Message: " + sl.getReasonPhrase() + "\n" +
                                             "Content Type: " + (entity == null ? "Unkown" : entity.getContentType()) + "\n" +
                                             "Body: " + (body == null ? "null" : body)
                                    );

                        // Return body if okay, otherwise return error code
                        if(statusCode == HttpStatus.SC_OK && body != null)
                        {
                            return Errorable.WithValue(body);
                        }
                        else
                        {
                            return Errorable.WithError(statusCode);
                        }
                    }
                }
            });

            if(response == null || response.isError())
            {
                if(response != null)
                    logger.error("Request failed without exception; Status: " + response.getError());

                return Optional.empty();
            }

            return Optional.of(response.getValue());
        }
        catch (ExecutionException e)
        {
            logger.error("Error while executing request; Url " + url + "\n", e);
            return Optional.empty();
        }
    }

    /**
     * Submit a task to the executor service
     * @param task Task to be executed
     * @param <T> Type of returned value from task
     * @return Future containing result of task
     */
    private static <T> T submit(PriorityCallable<T> task) throws ExecutionException
    {
        Future<T> result = executor.submit(new ScraperTaskSubmitter<>(task));

        try
        {
            return result.get();
        }
        catch (InterruptedException | CancellationException e)
        {
            logger.info("Scraper task was cancelled\n", e);
            return null;
        }
    }
}
