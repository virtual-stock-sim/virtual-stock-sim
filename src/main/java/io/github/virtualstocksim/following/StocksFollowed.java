package io.github.virtualstocksim.following;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.virtualstocksim.stock.Stock;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class StocksFollowed {

    private List<Follow> stocksFollowed;



    public StocksFollowed(List<Follow> stocksFollowed)
    {
        this.stocksFollowed = stocksFollowed;
    }

    public StocksFollowed(String s ){

    }

    public List<Follow> getStocksFollowed()
    {
        return this.stocksFollowed;
    }

    public void setStocksFollowed(List<Follow> stocksFollowed)
    {
        this.stocksFollowed = stocksFollowed;
    }



    public void setFollow(Follow newFollow)
    {
        this.stocksFollowed.add(newFollow);
    }

    public void removeFollow(Follow toRemove)
    {
        this.stocksFollowed.remove(toRemove);
    }



    public JsonArray getStocksFollowedFromJSON(StocksFollowed stocksFollowed){

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

    //will return a list of follow objects from a JSON string
    public List<Follow> parseFollowFromJSON(JsonArray j){

        List <Follow> Following = new LinkedList<>();
        for (JsonElement x: j) {

            Following.add(new Follow(x.getAsJsonObject().get("price").getAsBigDecimal(),  Stock.Find(x.getAsJsonObject().get("stock").getAsString()).get(),    Timestamp.valueOf(x.getAsJsonObject().get("timestamp").getAsString())));
        }
        return Following;
    }

}
