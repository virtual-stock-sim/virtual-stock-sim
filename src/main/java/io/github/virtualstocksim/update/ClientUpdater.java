package io.github.virtualstocksim.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.virtualstocksim.servlet.DataStreamServlet;
import io.github.virtualstocksim.stock.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Pushes new data to connected clients
 */
public class ClientUpdater
{
    private static final Logger logger = LoggerFactory.getLogger(ClientUpdater.class);

    public static void pushStockData(List<Stock> stocks, ConcurrentHashMap<String, AsyncContext> clients)
    {
        // Create JSON array of data to push to clients
        JsonArray dataArr = new JsonArray();
        for(Stock stock : stocks)
        {
            JsonObject stockObj = new JsonObject();
            stockObj.addProperty("symbol", stock.getSymbol());
            stockObj.addProperty("currPrice", stock.getCurrPrice());
            stockObj.addProperty("prevClose", stock.getPrevClose());
            stockObj.addProperty("currVolume", stock.getCurrVolume());
            stockObj.addProperty("lastUpdated", stock.getLastUpdated().toString());

            dataArr.add(stockObj);
        }

        String data = String.valueOf(dataArr);
        // Push the data to each currently connected client
        logger.info("Num Clients: " + clients.size());
        Enumeration<String> ids = clients.keys();
        while(ids.hasMoreElements())
        {
            DataStreamServlet.sendSimpleMessage(ids.nextElement(), data);
        }
    }

}