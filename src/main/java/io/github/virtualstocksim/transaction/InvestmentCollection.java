package io.github.virtualstocksim.transaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

//feel free to rename this class lol i'm tired
public class InvestmentCollection {
    public InvestmentCollection(List<Investment> investments){
        this.investmentList = investments;
    }
    public InvestmentCollection(String s ){
        this.setInvestments(this.stringToInvestmentList(s));
    }


    List<Investment> investmentList=new LinkedList<Investment>();
    //upon adding investment, check if already exists and add to numshares, otherwise add new share
    public void addInvestment(Investment in ) {
        for (Investment i : investmentList) {
            //look through all of the tickers, if there is a match just update the number of shares of that stock
            if (in.getTicker().equals(i.getTicker())) {
                i.setNumShares(i.getNumShares() + in.getNumShares());
                break;
            }
        }
        investmentList.add(in);
    }

    public void setInvestments(List <Investment> in ){
        this.investmentList= in;
    }

    public String buildJSON(){

        JsonArray ja = new JsonArray();
        for(Investment i  : this.investmentList){
            JsonObject jo = new JsonObject();                               //Shares, Ticker, Timestamp
            jo.addProperty("shares",i.getNumShares());
            jo.addProperty("ticker",i.getTicker());
            jo.addProperty("timestamp",i.getTimestamp().toString());
            ja.add(jo);
        }
        return ja.toString();
    }

    public List<Investment> stringToInvestmentList(String s ){
         List<Investment> temp = new LinkedList<>();

        JsonParser jsonParser = new JsonParser();
        JsonArray j = (JsonArray) jsonParser.parse(s);

        for(JsonElement x : j ){
            temp.add(new Investment(x.getAsJsonObject().get("shares").getAsInt(),x.getAsJsonObject().get("ticker").getAsString(), Timestamp.valueOf(x.getAsJsonObject().get("timestamp").getAsString())));
        }

        return temp;
    }
}
