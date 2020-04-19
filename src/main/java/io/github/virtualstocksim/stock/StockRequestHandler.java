package io.github.virtualstocksim.stock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.virtualstocksim.servlet.HttpRequestListener;
import io.github.virtualstocksim.update.ClientUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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
            JsonObject requestObj = JsonParser.parseString(request).getAsJsonObject();
            String requestType = requestObj.get("type").getAsString();

            JsonArray symbolArr = requestObj.get("symbols").getAsJsonArray();

            JsonArray dataArr = new JsonArray();
            if(requestType.equals("stock"))
            {
                for(JsonElement e : symbolArr)
                {
                    Stock stock = Stock.Find(e.getAsString()).orElse(null);
                    if(stock != null)
                    {
                        JsonObject stockObj = new JsonObject();
                        stockObj.addProperty("symbol", stock.getSymbol());
                        stockObj.addProperty("currPrice", stock.getCurrPrice());
                        stockObj.addProperty("prevClose", stock.getPrevClose());
                        stockObj.addProperty("currVolume", stock.getCurrVolume());
                        stockObj.addProperty("lastUpdated", stock.getLastUpdated().toString());

                        dataArr.add(stockObj);
                    }
                }
            }
            else if(requestType.equals("stockData"))
            {
                for(JsonElement e : symbolArr)
                {
                    List<StockData> datas = StockData.FindCustom("SELECT stock_data.id, stock_data.last_updated, stock_data.data FROM stock_data, stock WHERE stock.symbol = ? AND stock.data_id = stock_data.id", e.getAsString());
                    long ttl = ClientUpdater.getStockDataUpdateInterval().toMillis();
                    for(StockData d : datas)
                    {
                        JsonObject dataObj = JsonParser.parseString(d.getData()).getAsJsonObject();
                        dataObj.addProperty("ttl", ttl);
                        dataArr.add(dataObj);
                    }
                }
            }

            JsonObject returnObj = new JsonObject();
            returnObj.addProperty("type", requestType);
            returnObj.add("data", dataArr);

            resp.setContentType("application/json");
            logger.info(String.valueOf(returnObj));
            PrintWriter writer = resp.getWriter();
            writer.write(String.valueOf(returnObj));
            writer.flush();
        }
    }
}
