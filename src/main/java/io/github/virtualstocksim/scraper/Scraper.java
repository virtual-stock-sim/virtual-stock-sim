package io.github.virtualstocksim.scraper;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Scraper {

    private static final Logger logger = LoggerFactory.getLogger(Scraper.class);

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final long DELAY = TimeUnit.SECONDS.toMillis(10);
    private static final AtomicLong LAST_SCRAPE = new AtomicLong(0);

    //will be called within the getJson method
    public static String getDescription(String symbol) throws IOException {
        try
        {
            return submit(new GetDescriptionCallable(symbol));
        }
        catch (ExecutionException e)
        {
            Throwable cause = e.getCause();
            if(e.getCause() instanceof RuntimeException)
            {
                throw (RuntimeException) cause;
            }
            else
            {
                throw (IOException) cause;
            }
        }
    }

    //this method checks if the stock exists on Yahoo finance
    //Because Yahoo finance doesn't 404 if you look for a stock that isn't there
    //I just checked if it redirected to a "lookup" page rather than a "quote" page
    public static boolean checkStockExists(String symbol) throws IOException {
        try
        {
            Boolean result = submit(new CheckStockExistsCallable(symbol));
            return result == null ? false : result;
        }
        catch (ExecutionException e)
        {
            Throwable cause = e.getCause();
            if(e.getCause() instanceof RuntimeException)
            {
                throw (RuntimeException) cause;
            }
            else
            {
                throw (IOException) cause;
            }
        }
    }


    public static JsonObject getDescriptionAndHistory(String symbol, TimeInterval timeInterval) throws IOException, IllegalArgumentException, HttpStatusException
    {
        try
        {
            return submit(new GetDescAndHistCallable(symbol, timeInterval));
        }
        catch (ExecutionException e)
        {
            Throwable cause = e.getCause();
            if(cause instanceof IllegalArgumentException)
            {
                throw (IllegalArgumentException) cause;
            }
            else if(cause instanceof HttpStatusException)
            {
                throw (HttpStatusException) cause;
            }
            else if(cause instanceof RuntimeException)
            {
                throw (RuntimeException) cause;
            }
            else
            {
                throw (IOException) cause;
            }
        }
    }

    /**
     * Submit a task to the executor service
     * @param task Task to be executed
     * @param <T> Type of returned value from task
     * @return Future containing result of task
     */
    private static <T> T submit(Callable<T> task) throws ExecutionException
    {

        Future<T> result = executor.submit(() ->
                               {
                                   T _result;
                                   // Sleep for the delay if there is not DELAY time between now
                                   // and last task execution
                                   if(Instant.now().toEpochMilli() - DELAY <= LAST_SCRAPE.get())
                                   {
                                       TimeUnit.MILLISECONDS.sleep(DELAY);
                                   }
                                   // Execute the task
                                   _result = task.call();
                                   // Update the last task execution time
                                   LAST_SCRAPE.set(Instant.now().toEpochMilli());
                                   return _result;
                               });
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

    private static class GetDescriptionCallable implements Callable<String>
    {
        private final String symbol;
        public GetDescriptionCallable(String symbol)
        {
            this.symbol = symbol;
        }

        @Override
        public String call() throws IOException
        {
            logger.info("Getting company description for stock symbol: " + symbol);
            String URL = "https://finance.yahoo.com/quote/" + symbol + "/profile?p=" + symbol;
            Document doc = Jsoup.connect(URL).timeout(0).get();//timeout set to 0 indicates infinite
            Element s = doc.getElementsByClass("Mt(15px) Lh(1.6)").first();
            return s.text();
        }
    }

    private static class CheckStockExistsCallable implements Callable<Boolean>
    {
        private final String symbol;
        public CheckStockExistsCallable(String symbol)
        {
            this.symbol = symbol;
        }

        @Override
        public Boolean call() throws IOException
        {
            logger.info("Checking if stock symbol `" + symbol + "` exists");
            try {
                Connection.Response response = Jsoup.connect("https://finance.yahoo.com/quote/"+symbol).followRedirects(true).execute();
                int statusCode = response.statusCode();
                String redirect = response.url().toString();

                if (redirect.contains("lookup") || statusCode!=200) {
                    logger.info("status code: " + statusCode);
                    //was redirected to a lookup page, not on yahoo finance
                    return false;
                } else {
                    //stock is in yahoo finance
                    return true;
                }
            }catch (HttpStatusException e){ //this will account for MALFORMED symbols only (ex: starting name with a slash)
                logger.warn("Status code of " + e.getStatusCode() + " returned from " + e.getUrl() + "\n", e);
            }
            return false;
        }
    }

    private static class GetDescAndHistCallable implements Callable<JsonObject>
    {
        private final String symbol;
        private final TimeInterval timeInterval;
        public GetDescAndHistCallable(String symbol, TimeInterval timeInterval)
        {
            this.symbol = symbol;
            this.timeInterval = timeInterval;
        }

        @Override
        public JsonObject call() throws IOException, IllegalArgumentException, HttpStatusException
        {
            logger.info("Getting company description and price history for stock symbol `" + symbol + "`");
            long unixTime = Instant.now().toEpochMilli();
            //calculate seconds passed & sub from current unix time

            logger.info("Getting max price history for `" + symbol + "` with time interval of " + timeInterval.getPeriod());
            try
            {
                //since unix time calculates time from epoch, 0 and current time are really min and max values, do not need integer.max
                Connection.Response webResponse = Jsoup.connect("https://query1.finance.yahoo.com/v7/finance/download/" + symbol + "?period1=" + "0" + "&period2=" + unixTime + "&interval="+timeInterval.getPeriod()+"&events=history").followRedirects(true).execute();
                String responseCsv = webResponse.body();

                JsonObject result = new JsonObject();
                result.addProperty("symbol", symbol);
                result.addProperty("description", new GetDescriptionCallable(symbol).call());

                List<String> rowsList = new LinkedList<String>(Arrays.asList(responseCsv.split("\\n")));//get rows separated by line
                rowsList.remove(0);      //get rid of header

                List<String> col = new LinkedList<String>();

                for (String s : rowsList)
                {
                    col.addAll(Arrays.asList(s.split(",")));
                }

                JsonArray priceHistory = new JsonArray();
                for (int i = 0; i < col.size() - 6; i += 7) {
                    JsonObject jo = new JsonObject();
                    jo.addProperty("date", col.get(i));
                    jo.addProperty("open", col.get(i + 1));
                    jo.addProperty("high", col.get(i + 2));
                    jo.addProperty("low", col.get(i + 3));
                    jo.addProperty("close", col.get(i + 4));
                    jo.addProperty("adjclose", col.get(i + 5));
                    jo.addProperty("volume", col.get(i + 6));
                    priceHistory.add(jo);
                }
                result.add("history", priceHistory);

                return result;
            }
            catch (HttpStatusException e)
            {
                if(e.getStatusCode() == 404)
                {
                    logger.warn("Stock symbol `" + symbol + "` does not exist. Unable to retrieve description and history");
                    throw new IllegalArgumentException("Stock symbol " + symbol + " does not exist");
                }
                else
                {
                    throw e;
                }
            }
        }
    }
}
