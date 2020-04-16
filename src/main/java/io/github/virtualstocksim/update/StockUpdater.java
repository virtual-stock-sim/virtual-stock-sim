package io.github.virtualstocksim.update;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.virtualstocksim.config.Config;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.scraper.Scraper;
import io.github.virtualstocksim.scraper.TimeInterval;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.stock.StockData;
import io.github.virtualstocksim.stock.StockDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Pulls current stock data from iexcloud api and updates the database
 */
public class StockUpdater
{
    private static final Logger logger = LoggerFactory.getLogger(StockUpdater.class);

    /**
     * Update the frequently changing data for stocks
     * @param stocks Stocks to update
     */
    public static void updateStocks(List<Stock> stocks) throws UpdateException
    {
//        List<Stock> stocks = Stock.FindCustom("SELECT id, symbol FROM stocks");

        // Create String of all stock symbols separated by commas
        StringBuilder sb = new StringBuilder();
        Iterator<Stock> it = stocks.iterator();
        while(it.hasNext())
        {
            sb.append(it.next().getSymbol());
            // Add comma if there's another symbol
            if(it.hasNext()) sb.append(',');
        }

        JsonObject apiData = null;

        try
        {
            // It is assumed that stock symbols can be added onto the end
            // of the URL as a comma separated list to get the correct data
            URL apiUrl = new URL(Config.getConfig("stockapi.url") + sb.toString());
            URLConnection request = apiUrl.openConnection();

            if(!request.getContentType().contains("application/json"))
            {
                throw new IOException("Invalid api response");
            }

            // Read the data from the request
            try(InputStreamReader reader = new InputStreamReader((InputStream) request.getContent()))
            {
                JsonElement element = JsonParser.parseReader(reader);
                apiData = element.getAsJsonObject();
            }

        }
        catch (MalformedURLException e)
        {
            throw new UpdateException("Bad stock api URL", e);
        }
        catch (IOException e)
        {
            throw new UpdateException("Exception while acquiring/parsing data from api", e);
        }

        Timestamp lastUpdated = SQL.GetTimeStamp();
        JsonElement volume;
        for(Stock stock : stocks)
        {
            JsonObject data = apiData.getAsJsonObject(stock.getSymbol()).getAsJsonObject("quote");

            stock.setCurrPrice(data.get("latestPrice").getAsBigDecimal());
            stock.setPrevClose(data.get("previousClose").getAsBigDecimal());
            volume = data.get("volume");
            if(!volume.isJsonNull())
            {
                stock.setCurrVolume(volume.getAsInt());
            }
            stock.setPrevVolume(data.get("previousVolume").getAsInt());
            stock.setLastUpdated(lastUpdated);
        }

        try(Connection conn = StockDatabase.getConnection())
        {
            conn.setAutoCommit(false);

            for(Stock s : stocks)
            {
                s.update(conn);
            }

            conn.commit();
        }
        catch (SQLException e)
        {
            throw new UpdateException("Failed to commit some or all updated stock information", e);
        }

    }

    /**
     * Update the static data for stocks
     * @param stocks List of stocks to perform the update for
     * @param interval Desired price history time interval
     * @param delay Delay in between queries for each stock so the web scraper doesn't overload the site
     *              Should be somewhere around 15 seconds
     * @param delayLowerBound Lower bound for delay variation
     * @param delayUpperBound Upper bound for delay variation
     */
    public static void updateStockDatas(List<Stock> stocks, TimeInterval interval, int delay, int delayLowerBound, int delayUpperBound) throws UpdateException
    {
        Iterator<Stock> stockIt = stocks.iterator();
        while (stockIt.hasNext())
        {
            Stock stock = stockIt.next();
            // Get the stock data for the stock
            List<StockData> stockDatas = StockData.FindCustom("SELECT id FROM stock_data WHERE id = ?", stock.getStockDataId());
            if(!stockDatas.isEmpty())
            {
                StockData data = stockDatas.get(0);

                // Update the data in the DB
                try
                {
                    JsonObject scraperData = Scraper.getDescriptionAndHistory(stock.getSymbol(), interval);
                    Timestamp now = SQL.GetTimeStamp();
                    scraperData.addProperty("lastUpdated", now.toString());
                    data.setData(String.valueOf(scraperData));
                    data.setLastUpdated(now);

                    // Not using dedicated connection without auto commit since there's such a delay in-between update calls
                    data.update();
                }
                catch (IllegalArgumentException e)
                {
                    throw new UpdateException("Bad stock symbol: " + stock.getSymbol(), e);
                }
                catch (IOException e)
                {
                    throw new UpdateException(
                            "Exception while getting description and history for Stock with symbol " + stock.getSymbol(), e);
                }
                catch (SQLException e)
                {
                    throw new UpdateException(
                            "Exception while committing updates for Stock with symbol " + stock.getSymbol(), e);
                }

                // Only sleep if necessary
                if (stockIt.hasNext())
                {
                    pauseStockDataUpdate(delay, delayUpperBound, delayLowerBound);
                }
            }
        }
    }

    private static void pauseStockDataUpdate(int delay, int delayUpperBound, int delayLowerBound) throws UpdateException
    {
        // Attempt to sleep
        int attempts = 0;
        int maxAttempts = 2;
        boolean sleepSuccess = false;
        while(!sleepSuccess)
        {
            Random r = new Random();
            try
            {
                int randDelay = delay + r.nextInt(delayUpperBound - delayLowerBound + 1) + delayLowerBound;
                logger.info("Sleeping for " + randDelay + " seconds");
                TimeUnit.SECONDS.sleep(randDelay);
                sleepSuccess = true;
            }
            catch (InterruptedException e)
            {
                if(attempts < maxAttempts)
                {
                    logger.warn("Exception while sleeping on attempt #" + attempts + " while updating static stock data. Trying again...\n", e);
                    ++attempts;
                }
                else
                {
                    throw new UpdateException("Max exceptions reached for attempting to sleep while updating static stock data", e);
                }
            }
        }
    }
}
