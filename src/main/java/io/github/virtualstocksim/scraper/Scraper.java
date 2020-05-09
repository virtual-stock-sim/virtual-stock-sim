package io.github.virtualstocksim.scraper;


import com.google.gson.*;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.stock.StockDatabase;
import io.github.virtualstocksim.stock.stockrequest.StockResponseCode;
import io.github.virtualstocksim.util.Result;
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
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class Scraper
{

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
    public static Result<String, StockResponseCode> getDescription(String symbol, Priority priority)
    {
        if(symbolInvalid(symbol))
        {
            return Result.WithError(StockResponseCode.INVALID_SYMBOL);
        }

        logger.info("Submitting company description request for '" + symbol + "'");
        String url = "https://finance.yahoo.com/quote/" + symbol + "/profile";
        Result<Connection.Response, Integer> response = getJsoupResponse(priority, url);

        if(response.isError())
        {
            if(response.getError() == HttpStatus.SC_NOT_FOUND)
                return Result.WithError(StockResponseCode.SYMBOL_NOT_FOUND);
            else
                return Result.WithError(StockResponseCode.SERVER_ERROR);
        }

        try
        {
            // Attempt to get company description from page
            Document doc = response.getValue().parse();
            Element elem = doc.getElementsByClass("Mt(15px) Lh(1.6)").first();
            String description;

            // If elements containing description where found and the description isn't empty
            if(elem != null && (description = elem.text()).trim().length() > 0)
            {
                return Result.WithValue(description);
            }
            else
            {
                return Result.WithError(StockResponseCode.SYMBOL_NOT_FOUND);
            }
        }
        catch (IOException e)
        {
            logger.error("Unable to parse returned document; URL: " + url + "\n", e);
            return Result.WithError(StockResponseCode.SERVER_ERROR);
        }
    }

    /**
     * Check if the stock exists online
     * @param symbol Stock to search for
     * @param priority Execution priority of the request
     * @return If the stock exists or error code
     */
    public static Result<Boolean, StockResponseCode> checkStockExists(String symbol, Priority priority)
    {
        if(symbolInvalid(symbol))
        {
            return Result.WithError(StockResponseCode.INVALID_SYMBOL);
        }

        logger.info("Submitting existence check for '" + symbol + "'");

        try
        {
            Result<Boolean, Integer> result = submit(new PriorityCallable<Result<Boolean, Integer>>(priority)
            {
                @Override
                public Result<Boolean, Integer> call() throws Exception
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
                            return Result.WithValue(true);
                        }
                        else if(statusCode == HttpStatus.SC_NOT_FOUND)
                        {
                            return Result.WithValue(false);
                        }
                        else
                        {
                            return Result.WithError(statusCode);
                        }
                    }
                }
            });


            if(result == null)
            {
                return Result.WithError(StockResponseCode.SERVER_ERROR);
            }
            else if(result.isError())
            {
                if(result.getError() == HttpStatus.SC_NOT_FOUND)
                    return Result.WithValue(false);
                else
                    return Result.WithError(StockResponseCode.SERVER_ERROR);
            }
            else
            {
                return Result.WithValue(result.getValue());
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
            return Result.WithError(StockResponseCode.SERVER_ERROR);
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
    public static Result<JsonObject, StockResponseCode> getDescriptionAndHistory(String symbol, TimeInterval timeInterval, Priority priority)
    {
        if(symbolInvalid(symbol))
        {
            return Result.WithError(StockResponseCode.INVALID_SYMBOL);
        }

        // Get the description
        Result<String, StockResponseCode> descriptionResp = getDescription(symbol, priority);
        if(descriptionResp.isError())
            return Result.WithError(descriptionResp.getError());

        logger.info("Submitting price history request for '" + symbol + "'");

        // Get the price history CSV file as a string
        String url = "https://query1.finance.yahoo.com/v7/finance/download/" + symbol +
                        "?period1=0&period2=" + Instant.now().toEpochMilli() +
                        "&interval=" + timeInterval.getPeriod()+"&events=history";
        Optional<String> response = getHttpBody(priority, url);

        if(!response.isPresent())
        {
            logger.error("Error while getting price history for '" + symbol + "'");
            return Result.WithError(StockResponseCode.SERVER_ERROR);
        }

        String csv = response.get();
        // Make sure the response is present and the body isn't empty
        if(csv.isEmpty())
            return Result.WithError(StockResponseCode.SERVER_ERROR);

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

        return Result.WithValue(result);
    }

    public static Collection<Result<Stock, StockResponseCode>> getCurrentData(Set<String> symbols, Priority priority) throws IOException
    {
        if(symbols.isEmpty())
            throw new IllegalArgumentException("Symbol list can't be empty");

        Map<String, Result<Stock, StockResponseCode>> resultMap = new HashMap<>();
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
                resultMap.put(symbol, Result.WithError(StockResponseCode.SERVER_ERROR));
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
            // Get the site response as a json object
            JsonElement parsedResponse = JsonParser.parseString(response.get());
            JsonObject responseObj = JsonUtil.getAs(parsedResponse, JsonElement::getAsJsonObject)
                                             .getOrNull(err ->
                                                        { throw new IOException("Exception while getting response as JsonObject: " + err); });

            // Extract the quoteResponse object from the site response
            JsonObject quoteResponse = JsonUtil.getMemberAs(responseObj, "quoteResponse", JsonElement::getAsJsonObject)
                                               .getOrNull(err ->
                                                          { throw new IOException("Error getting current data 'quoteResponse' parent member: " + err); });

            // Make sure the site didn't return an error
            JsonUtil.getMemberAs(quoteResponse, "error", JsonElement::getAsJsonPrimitive)
                    .getOrNull(err ->
                               {
                                   if(err != JsonError.NULL && err != JsonError.NONEXISTENT)
                                   {
                                       throw new IOException("Site returned error: " + err);
                                   }
                               });

            // Extract the array of quote results from the quoteResponse
            JsonArray resultArr = JsonUtil.getMemberAs(quoteResponse, "result", JsonElement::getAsJsonArray)
                                          .getOrNull(err ->
                                                     { throw new IOException("Error getting result array of current data: " + err); });

            for(JsonElement element : resultArr)
            {
                logger.trace("Quote Element: " + element);

                // Get the quote as a JsonObject
                JsonObject quote = JsonUtil.getAs(element, JsonElement::getAsJsonObject)
                                           .getOrNull(err -> logger.error("Error getting quote: " + err));

                // Extract the quote elements if the quote is valid
                if(quote != null)
                {
                    // Extract the stock symbol from the quote
                    String symbol = JsonUtil.getMemberAs(quote, "symbol", JsonElement::getAsString)
                                            .getOrNull(err -> logger.error("Couldn't retrieve symbol: " + err));

                    // Extract the current price from the quote
                    BigDecimal currPrice = JsonUtil.getMemberAs(quote, "regularMarketPrice", JsonElement::getAsBigDecimal)
                                                   .getOrNull(err -> logger.error("Couldn't retrieve regularMarketPrice: " + err));

                    // Extract the previous close price from the quote
                    BigDecimal previousClose = JsonUtil.getMemberAs(quote, "regularMarketPreviousClose", JsonElement::getAsBigDecimal)
                                                       .getOrNull(err -> logger.error("Couldn't retrieve previous market price: " + err));

                    // Extract the current volume from the quote
                    Integer currVolume = JsonUtil.getMemberAs(quote, "regularMarketVolume", JsonElement::getAsInt)
                                             .getOrNull(err -> logger.error("Could not retrieve current market volume: " + err));

                    resultMap.put(symbol, Result.WithValue(new Stock(symbol, currPrice, previousClose, currVolume, null)));
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
     * @return Response from the url or an Http status code
     */
    private static Result<Connection.Response, Integer> getJsoupResponse(Priority priority, String url)
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

            return response == null ? Result.WithError(HttpStatus.SC_INTERNAL_SERVER_ERROR) : Result.WithValue(response);
        }
        catch (ExecutionException e)
        {
            Throwable cause = e.getCause();
            if(cause instanceof MalformedURLException)
            {
                logger.error("Bad URL: " + url + "\n", e);
                return Result.WithError(HttpStatus.SC_BAD_REQUEST);
            }
            else if(cause instanceof HttpStatusException)
            {
                HttpStatusException exception = (HttpStatusException) cause;
                logger.error("Response for URL was '" + exception.getStatusCode() +"' not '200 OK'; URL: " + url + "\n", e);
                return Result.WithError(exception.getStatusCode());
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
            return Result.WithError(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static Optional<String> getHttpBody(Priority priority, String url)
    {
        try
        {
            // Submit the task
            Result<String, Integer> response = submit(new PriorityCallable<Result<String, Integer>>(priority)
            {
                @Override
                public Result<String, Integer> call() throws Exception
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
                        logger.trace("Response to: " + url + "\n\n" +
                                             "Status Code: " + statusCode + "\n" +
                                             "Status Message: " + sl.getReasonPhrase() + "\n" +
                                             "Content Type: " + (entity == null ? "Unknown" : entity.getContentType()) + "\n" +
                                             "Body: " + (body == null ? "null" : body) + "\n"
                                    );

                        // Return body if okay, otherwise return error code
                        if(statusCode == HttpStatus.SC_OK && body != null)
                        {
                            return Result.WithValue(body);
                        }
                        else
                        {
                            return Result.WithError(statusCode);
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
