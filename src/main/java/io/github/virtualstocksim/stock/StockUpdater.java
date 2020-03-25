package io.github.virtualstocksim.stock;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.virtualstocksim.config.Config;
import io.github.virtualstocksim.database.SQL;
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
 * Pulls current stock data from iexcloud api and updates the database
 */
public class StockUpdater
{
    private static final Logger logger = LoggerFactory.getLogger(StockUpdater.class);

    public StockUpdater()
    {

    }

    public void update()
    {
        List<Stock> stocks = Stock.FindCustom("SELECT id, symbol FROM stocks");

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
            logger.error("Bad stock api URL\n", e);
            return;
        }
        catch (IOException e)
        {
            logger.error("Exception while acquiring/parsing data from api\n", e);
            return;
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
            logger.error("Failed to commit some or all updated stock information\n", e);
            return;
        }

    }

    Integer t()
    {
        return null;
    }

}
