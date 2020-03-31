package io.github.virtualstocksim.following;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.stock.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class StocksFollowedController {
    private static final Logger logger = LoggerFactory.getLogger(StocksFollowedController.class);
    JsonArray array = new JsonArray();
    JsonObject object;

   private StocksFollowed model = null;



    public JsonArray getFollowJSON(StocksFollowed stocksFollowed){

        JsonArray ja = new JsonArray();

        for(Follow x : stocksFollowed.getStocksFollowed()){
            JsonObject jo = new JsonObject();
            jo.addProperty("stock", x.getStock().getSymbol());
            jo.addProperty("price",x.getStock().getCurrPrice());
            jo.addProperty("timestamp",x.getTimeStamp().toString());
            jo.addProperty("invested","true");
            ja.add(jo);
        }

        return ja;
    }
   /* TODO: Here is why Follow should also probably have another constructor again to make this look better/better programming style
    */
    //will return a list of follow objects from a JSON string
    public List<Follow> parseFollowFromJSON(JsonArray j){

        List <Follow> Following = new LinkedList<>();
       for (JsonElement x: j) {

           Following.add(new Follow(x.getAsJsonObject().get("price").getAsBigDecimal(),  Stock.Find(x.getAsJsonObject().get("stock").getAsString()).get(),    Timestamp.valueOf(x.getAsJsonObject().get("timestamp").getAsString())));
       }
        return Following;
    }
/*
    public static void main(String[] args){
        //This driver code is for testing only
        //will remove after debugging
        List<Follow>followList = new LinkedList<Follow>();
        followList.add(new Follow(new BigDecimal(100), Stock.Find(1).get(), SQL.GetTimeStamp()));
        followList.add(new Follow(new BigDecimal(498), Stock.Find(2).get(),SQL.GetTimeStamp()));
        followList.add(new Follow(new BigDecimal(498), Stock.Find(3).get(),SQL.GetTimeStamp()));
        followList.add(new Follow(new BigDecimal(498), Stock.Find(4).get(),SQL.GetTimeStamp()));
        followList.add(new Follow(new BigDecimal(498), Stock.Find(5).get(),SQL.GetTimeStamp()));
        StocksFollowed model = new StocksFollowed(followList);
        StocksFollowedController testController = new StocksFollowedController();
        //this should parse the follows to JSON, back to follows, and then print

        /*uncomment here to print!
           List<Follow> exampleList = testController.parseFollowFromJSON(testController.getFollowJSON(model));
        //logger.info(testController.getFollowJSON(model));
       for(Follow x : exampleList){
            logger.info(x.getInitialPrice().toString() + " " + x.getStock().getSymbol() + " "+x.getTimeStamp());
        }




    }*/



}
