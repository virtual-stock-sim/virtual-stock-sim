package io.github.virtualstocksim.following;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.virtualstocksim.stock.Stock;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StocksFollowed {

    private List<Follow> stocksFollowed;

    public StocksFollowed(List<Follow> stocksFollowed)
    {
        this.stocksFollowed = stocksFollowed;
    }
    public StocksFollowed(String s){
        //method takes care of null check
        setStocksFollowed(this.stringToFollowObjects(s));

    }
    public List<Follow> getStocksFollowed()
    {
        return this.stocksFollowed;
    }

    public void setStocksFollowed(List<Follow> stocksFollowed)
    {
        this.stocksFollowed = stocksFollowed;
    }

    public void updateStocksFollowed(String s){
        if(s.equals("")){
            this.stocksFollowed.clear();
            //System.out.println("followed is now empty");
        }else {
            this.stocksFollowed = stringToFollowObjects(s);
        }
    }

    public void setFollow(Follow newFollow)
    {
        this.stocksFollowed.add(newFollow);
    }



    //to change following objects into String for DB storage
    public String followObjectsToString(){
        String s = "";
        for (Follow f : this.getStocksFollowed()){
            s+=f.getInitialPrice()+"," + f.getStock().getSymbol()+ "," +f.getPercentChange()+"," + f.getTimeStamp() +  ";";
        }
        return s;
    }


    public void removeFollow(String symbol){
        for(int i =0; i< stocksFollowed.size();i++){
            if(stocksFollowed.get(i).getStock().getSymbol().toUpperCase().equals(symbol.toUpperCase())){
                stocksFollowed.remove(i);
            }
        }
    }

    public boolean containsStock(String symbol){
        if(followObjectsToString().contains(symbol)){
            return true;
        }else {
            return false;
        }
    }
    //Will be stored in DB as a plain string formatted with ; and , instead of JSON to avoid unnecessary overhead
    //to create following objects from DB string into Follow objects (to be used in string constructor)
    public List stringToFollowObjects(String input){
        LinkedList<Follow> temp = new LinkedList<>();
        if(input==null || input.trim().isEmpty()){
            return temp;
        }
        for(String s : input.split(";")){
            LinkedList <String> args = new LinkedList<>();
            args.addAll(Arrays.asList(s.split(",")));
            temp.add(new Follow( new BigDecimal(args.get(0).toString()), Stock.Find(args.get(1)).get(), Timestamp.valueOf(args.get(3))));
        }
        return  temp;
    }
    public void addFollow(Follow f){
        this.stocksFollowed.add(f);
    }


    //deprecated because not storing as a JSON string anymore....
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

    //deprecated because not storing as a JSON string anymore....
    //will return a list of follow objects from a JSON string
    public List<Follow> parseFollowFromJSON(JsonArray j){
        List <Follow> Following = new LinkedList<>();
        for (JsonElement x: j) {

            Following.add(new Follow(x.getAsJsonObject().get("price").getAsBigDecimal(),  Stock.Find(x.getAsJsonObject().get("stock").getAsString()).get(), Timestamp.valueOf(x.getAsJsonObject().get("timestamp").getAsString())));
        }
        return Following;
    }

}
