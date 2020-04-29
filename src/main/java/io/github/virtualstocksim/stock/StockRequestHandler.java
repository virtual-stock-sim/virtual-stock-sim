package io.github.virtualstocksim.stock;

import com.google.gson.*;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.scraper.Scraper;
import io.github.virtualstocksim.scraper.TimeInterval;
import io.github.virtualstocksim.servlet.HttpRequestListener;
import io.github.virtualstocksim.update.ClientUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockRequestHandler implements HttpRequestListener
{
    private static final Logger logger = LoggerFactory.getLogger(StockRequestHandler.class);
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    @Override
    public void onGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {

    }

    @Override
    public void onPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        String request = req.getParameter("dataRequest");
        if(request != null)
        {
            // Convert the request into a json object
            JsonObject requestObj = JsonParser.parseString(request).getAsJsonObject();
            // Get the type of the request (stock or stock data)
            String requestType = requestObj.get("type").getAsString();
            // Get the array of stock symbols
            JsonArray symbols = requestObj.get("symbols").getAsJsonArray();

            JsonElement responseData;
            switch (requestType)
            {
                case "stock":
                    responseData = onStockRequest(symbols);
                    break;
                case "stockData":
                    responseData = onStockDataRequest(symbols);
                    break;
                case "stockSearch":
                    responseData = onStockSearchRequest(symbols);
                    break;
                default:
                    responseData = JsonNull.INSTANCE;
            }

            JsonObject returnObj = new JsonObject();
            returnObj.addProperty("type", requestType);
            returnObj.add("data", responseData);

            resp.setContentType("application/json");

            // Write the response
            PrintWriter writer = resp.getWriter();
            writer.write(String.valueOf(returnObj));
            writer.flush();
        }
    }

    private static JsonArray onStockRequest(JsonArray symbols)
    {

        JsonArray dataArr = new JsonArray();
        // Find each requested stock, add it to a json object, then add the object to the response array
        for(JsonElement elem : symbols)
        {
            Stock.Find(elem.getAsString()).ifPresent(stock -> dataArr.add(stock.getAsJsonObject()));
        }

        return dataArr;
    }

    private static JsonArray onStockDataRequest(JsonArray symbols)
    {
        // Array containing stock datas to be sent in the response
        JsonArray dataArr = new JsonArray();

        // Find each requested stock data, add it to a json object, then add the object to the response array
        for(JsonElement elem : symbols)
        {
            List<StockData> datas = StockData.FindCustom("SELECT stock_data.id, stock_data.last_updated, stock_data.data FROM stock_data, stock WHERE stock.symbol = ? AND stock.data_id = stock_data.id", elem.getAsString());
            long ttl = ClientUpdater.getStockDataUpdateInterval().toMillis();
            for(StockData d : datas)
            {
                JsonObject dataObj = d.getAsJsonObject();
                dataObj.addProperty("ttl", ttl);
                dataArr.add(dataObj);
            }
        }

        return dataArr;
    }

    private static final BigDecimal BIGD_NEG_ONE = new BigDecimal("-1.0");

    private static JsonArray onStockSearchRequest(JsonArray symbols) throws IOException
    {
        JsonArray dataArr = new JsonArray();

        for(JsonElement elem : symbols)
        {

            String symbol = elem.getAsString();

            JsonObject returnObj = new JsonObject();
            returnObj.addProperty("symbol", symbol);
            int errorCode = -1;

            Stock stock = Stock.Find(symbol).orElse(null);
            // If the stock is found, add it to the results
            if(stock != null)
            {
                // Only return the stock if it exists in the DB since it is likely that
                // the data will already be cached client-side
                returnObj.add("stock", stock.getAsJsonObject());
            }
            // Otherwise, check if the web scraper can find that it exists at all
            else if(Scraper.checkStockExists(symbol))
            {

                // Retrieve and add the stock data to the database
                JsonObject descAndHist = Scraper.getDescriptionAndHistory(symbol, TimeInterval.ONEMONTH);
                StockData stockData = StockData.Create(String.valueOf(descAndHist), SQL.GetTimeStamp()).orElse(null);

                if (stockData != null)
                {
                    stock = Stock.Create(symbol, BIGD_NEG_ONE, BIGD_NEG_ONE, -1, -1, stockData.getId(), null).orElse(null);

                    if(stock != null)
                    {
                        // Send default stock parameters to client to indicate that new stock data will be
                        // send shortly with the next update cycle
                        returnObj.add("stock", stock.getAsJsonObject());
                        // If the stock is new to the database, send over the data as well since the client will
                        // likely be requesting the data soon if not immediately after this request anyway
                        JsonObject stockDataObj = stockData.getAsJsonObject();
                        stockDataObj.addProperty("ttl", ClientUpdater.getStockDataUpdateInterval().toMillis());
                        returnObj.add("stockData", stockDataObj);
                    }
                    else
                    {
                        errorCode = 500;
                    }
                }
                else
                {
                    errorCode = 500;
                }
            }
            else
            {
                errorCode = 404;
            }

            if(errorCode != -1)
            {
                returnObj.addProperty("error", errorCode);
            }

            dataArr.add(returnObj);
        }

        return dataArr;
    }
}
