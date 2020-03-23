package io.github.virtualstocksim.stock;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.virtualstocksim.config.Config;
import io.github.virtualstocksim.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
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

    public void update() throws IOException
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


        for(Stock stock : stocks)
        {
            JsonObject data = apiData.getAsJsonObject(stock.getSymbol()).getAsJsonObject("quote");

            stock.setCurrPrice(data.get("latestPrice").getAsBigDecimal());
            stock.setLastUpdated(Util.GetTimeStamp());
        }

        try
        {
            for(Stock s : stocks)
            {
                s.commit();
            }
        }
        catch (SQLException e)
        {
            logger.error("Failed to commit some or all updated stock information\n", e);
        }

    }



}
