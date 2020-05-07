package io.github.virtualstocksim.update;


import com.google.gson.JsonObject;
import io.github.virtualstocksim.config.Config;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.scraper.Scraper;
import io.github.virtualstocksim.scraper.TimeInterval;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.stock.StockData;
import io.github.virtualstocksim.stock.StockDatabase;
import io.github.virtualstocksim.stock.stockrequest.StockResponseCode;
import io.github.virtualstocksim.util.Errorable;
import io.github.virtualstocksim.util.priority.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        logger.info("Running periodic stock update");

        Map<String, Stock> stockMap = new HashMap<>(stocks.size());
        for(Stock s : stocks)
        {
            stockMap.put(s.getSymbol(), s);
        }

        try
        {
            Collection<Errorable<Stock, StockResponseCode>> response = Scraper.getCurrentData(stockMap.keySet(), Priority.MEDIUM);
            try(Connection conn = StockDatabase.getConnection())
            {
                conn.setAutoCommit(false);

                for(Errorable<Stock, StockResponseCode> r : response)
                {
                    if(r.isError())
                    {
                        logger.error("Error getting market data for stock: " + r.getError());
                    }
                    else
                    {
                        Stock curr = r.getValue();
                        Stock old = stockMap.getOrDefault(curr.getSymbol(), null);
                        if(old != null)
                        {
                            old.setCurrPrice(curr.getCurrPrice());
                            old.setPrevClose(curr.getPrevClose());
                            old.setPrevVolume(curr.getPrevVolume());
                            old.setLastUpdated(SQL.GetTimeStamp());

                            old.update(conn);
                        }
                    }
                }

                conn.commit();
            }
            catch (SQLException e)
            {
                throw new UpdateException("Failed to commit some or all updated stock information", e);
            }
        }
        catch (IOException e)
        {
            throw new UpdateException("Error getting current stock market data", e);
        }
    }

    /**
     * Update the static data for stocks
     * @param stocks List of stocks to perform the update for
     * @param interval Desired price history time interval
     */
    public static void updateStockDatas(List<Stock> stocks, TimeInterval interval) throws UpdateException
    {
        logger.info("Running periodic stock data update");
        for (Stock stock : stocks)
        {
            // Get the stock data for the stock
            List<StockData> stockDatas = StockData.FindCustom("SELECT id FROM stock_data WHERE id = ?", stock.getStockDataId());
            if (!stockDatas.isEmpty())
            {
                StockData data = stockDatas.get(0);

                // Update the data in the DB
                try
                {
                    Errorable<JsonObject, StockResponseCode> scraperData = Scraper.getDescriptionAndHistory(stock.getSymbol(), interval, Priority.LOW);
                    if(scraperData.isError())
                    {
                        logger.error("Periodic update failed for StockData Id: " + data.getId() + " in scraper phase. Error code: " + scraperData.getError());
                    }
                    JsonObject dataJson = scraperData.getValue();
                    Timestamp now = SQL.GetTimeStamp();
                    dataJson.addProperty("lastUpdated", now.toString());
                    data.setData(String.valueOf(dataJson));
                    data.setLastUpdated(now);

                    // Not using dedicated connection without auto commit since there's such a delay in-between update calls
                    data.update();
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