package io.github.virtualstocksim.scraper;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.virtualstocksim.stock.StockDatabase;
import io.github.virtualstocksim.stock.stockrequest.StockResponseCode;
import io.github.virtualstocksim.util.Errorable;
import io.github.virtualstocksim.util.priority.PriorityCallable;
import io.github.virtualstocksim.util.priority.Priority;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
        Optional<Connection.Response> response = getResponse(priority, url);

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
                response = getResponse(priority != Priority.LOW ? Priority.URGENT : priority, url);

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
        Optional<Connection.Response> response = getResponse(priority, "https://finance.yahoo.com/quote/" + symbol);

        if(response.isPresent())
        {
            return Errorable.WithValue(true);
        }
        else
        {
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
        // Get the price history
        Optional<Connection.Response> priceHistoryResp = getResponse(Priority.URGENT,
                                                             "https://query1.finance.yahoo.com/v7/finance/download/" + symbol +
                                                                     "?period1=0&period2=" + Instant.now().toEpochMilli() +
                                                                     "&interval=" + timeInterval.getPeriod()+"&events=history"
                                                            );


        String csv;
        // Make sure the response is present and the body isn't empty
        if(!priceHistoryResp.isPresent() || (csv = priceHistoryResp.get().body()).isEmpty())
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

    private static boolean symbolInvalid(String symbol)
    {
        return symbol.contains("^") || symbol.length() > StockDatabase.getMaxSymbolLen();
    }

    /**
     * Submit request and get response
     * @param priority Execution priority of the connection request
     * @param url Url of the target
     * @param method HTTP request type
     * @return Response from the url
     */
    private static Optional<Connection.Response> getResponse(Priority priority, Connection.Method method, String url)
    {
        try
        {
            Connection.Response response = submit(new PriorityCallable<Connection.Response>(priority.getPriority())
            {
                @Override
                public Connection.Response call() throws Exception
                {
                    logger.info("Sending " + method.name() + " request to: " + url);
                    Connection.Response r = Jsoup.connect(url).method(method).followRedirects(true).execute();
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
                logger.error("Exception executing request; Priority: " + priority.getPriority() + " URL: " + url + "\n", e);
            }
            return Optional.empty();
        }
    }

    /**
     * Submit GET request and get the response
     * @param priority Execution priority of the connection request
     * @param url Url of the target
     * @return Response from the url
     */
    private static Optional<Connection.Response> getResponse(Priority priority, String url)
    {
        return getResponse(priority, Connection.Method.GET, url);
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
