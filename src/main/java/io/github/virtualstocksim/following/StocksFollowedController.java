package io.github.virtualstocksim.following;
import io.github.virtualstocksim.following.Follow;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StocksFollowedController {
    private static final Logger logger = LoggerFactory.getLogger(StocksFollowedController.class);
    JsonArray array = new JsonArray();
    JsonObject object;

   private StocksFollowed model = null;



    public String buildJSON(StocksFollowed stocksFollowed){

        JsonArray ja = new JsonArray();

        for(Follow x : stocksFollowed.getStocksFollowed()){
            JsonObject jo = new JsonObject();
            jo.addProperty("stock", x.getStock().getSymbol());
            jo.addProperty("price",x.getStock().getCurrPrice());
            jo.addProperty("timestamp",x.getTimeStamp().toString());
            jo.addProperty("invested","true");
            ja.add(jo);
        }

        return ja.toString();
    }


    public static void main(String[] args){
        //This runner is just for checking if buildJSON is goin good
        //sorry :)
        List<Follow>followList = new LinkedList<Follow>();
        followList.add(new Follow(new BigDecimal(100), Stock.Find(1).get(), Util.GetTimeStamp()));
        followList.add(new Follow(new BigDecimal(498), Stock.Find(2).get(),Util.GetTimeStamp()));
        followList.add(new Follow(new BigDecimal(498), Stock.Find(3).get(),Util.GetTimeStamp()));
        followList.add(new Follow(new BigDecimal(498), Stock.Find(4).get(),Util.GetTimeStamp()));
        followList.add(new Follow(new BigDecimal(498), Stock.Find(5).get(),Util.GetTimeStamp()));
        StocksFollowed model = new StocksFollowed(followList);
        StocksFollowedController testController = new StocksFollowedController();
        logger.info(testController.buildJSON(model));
    }


    //StocksFollowed model = new StocksFollowed();
    //iterate over all followed stocks, and create json obects
    //JSON.add()
}
