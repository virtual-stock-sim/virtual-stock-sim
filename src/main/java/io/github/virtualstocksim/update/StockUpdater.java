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

/**
 * TODO: The update methods shouldn't abort the entire update process if
 *  an error occurs for one stock
 */

/**
 * Pulls current stock data from iexcloud api and updates the database
 */
public class StockUpdater
{
    private static final Logger logger = LoggerFactory.getLogger(StockUpdater.class);
    private static final String apiUrl = Config.getConfig("stockapi.url");

    /**
     * Update the frequently changing data for stocks
     * @param stocks Stocks to update
     */
    public static void updateStocks(List<Stock> stocks) throws UpdateException
    {

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
            logger.info("Requesting new data from api");
            // It is assumed that stock symbols can be added onto the end
            // of the URL as a comma separated list to get the correct data
            URL url = new URL(apiUrl + sb.toString());
            URLConnection request = url.openConnection();

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
        logger.info(String.valueOf(apiData));
        for(Stock stock : stocks)
        {
            if(apiData.has(stock.getSymbol()))
            {
                JsonObject data = apiData.getAsJsonObject(stock.getSymbol()).getAsJsonObject("quote");

                JsonElement latestPrice = data.get("latestPrice");
                if(!latestPrice.isJsonNull())
                {
                    stock.setCurrPrice(latestPrice.getAsBigDecimal());
                }

                JsonElement previousClose = data.get("previousClose");
                if(!previousClose.isJsonNull())
                {
                    stock.setPrevClose(previousClose.getAsBigDecimal());
                }

                JsonElement volume = data.get("latestVolume");
                if(!volume.isJsonNull())
                {
                    stock.setCurrVolume(volume.getAsInt());
                }

                JsonElement previousVolume = data.get("previousVolume");
                if(!previousClose.isJsonNull())
                {
                    stock.setPrevVolume(previousVolume.getAsInt());
                }

                stock.setLastUpdated(SQL.GetTimeStamp());
            }
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
     */
    public static void updateStockDatas(List<Stock> stocks, TimeInterval interval) throws UpdateException
    {
        for(Stock stock : stocks)
        {
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
            }
        }
    }
}
