package io.github.virtualstocksim.stock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.virtualstocksim.servlet.DataStreamServlet;
import io.github.virtualstocksim.servlet.HttpRequestListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
            String id = req.getHeader("id");
            if(id != null)
            {
                pool.submit(new StockRequestProcessor(id, request));
            }
        }
    }

    private static class StockRequestProcessor implements Runnable
    {
        private static final Logger logger = LoggerFactory.getLogger(StockRequestProcessor.class);
        private final String asyncContextId;
        private final String request;
        public StockRequestProcessor(String asyncContextId, String request)
        {
            this.asyncContextId = asyncContextId;
            this.request = request;
        }

        @Override
        public void run()
        {
            JsonObject requestObj = JsonParser.parseString(request).getAsJsonObject();
            String requestType = requestObj.get("type").getAsString();

            JsonArray symbolArr = requestObj.get("symbols").getAsJsonArray();

            JsonArray dataArr = new JsonArray();
            if(requestType.equals("stocks"))
            {

            }
            else if(requestType.equals("stockDatas"))
            {
                for(JsonElement e : symbolArr)
                {
                    logger.info(e.getAsString());
                    List<StockData> datas = StockData.FindCustom("SELECT stock_data.id, stock_data.last_updated, stock_data.data FROM stock_data, stock WHERE stock.symbol = ? AND stock.data_id = stock_data.id", e.getAsString());
                    for(StockData d : datas)
                    {
                        dataArr.add(d.getData());
                    }
                }
            }

            JsonObject returnObj = new JsonObject();
            returnObj.addProperty("type", requestType);
            returnObj.add("data", dataArr);

            logger.info(String.valueOf(returnObj));
            DataStreamServlet.sendSimpleMessage(asyncContextId, String.valueOf(returnObj));
        }
    }
}
