package io.github.virtualstocksim.transaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;


public class InvestmentCollection {
    private static final Logger logger = LoggerFactory.getLogger(InvestmentCollection.class);
    private List<Investment> investmentList;

    public InvestmentCollection(List<Investment> investments){
        this.investmentList =new LinkedList<>(investments);
    }
    public InvestmentCollection(String s ){
        this.setInvestments(this.stringToInvestmentList(s));
    }
    public List<Investment> getInvestments(){
        return this.investmentList;
    }

    public void setInvestments(List <Investment> in ){
        this.investmentList= in;
    }


    //upon adding investment, check if already exists and add to numshares, otherwise add new share
    public void addInvestment(Investment in ) {
        if(isInvested(in.getTicker())) {
            for (Investment i : investmentList) {
                //look through all of the tickers, if there is a match just update the number of shares of that stock
                if (in.getTicker().equals(i.getTicker())) {
                    i.setNumShares(i.getNumShares() + in.getNumShares());
                    break;
                }
            }
        }else {
            investmentList.add(in);
        }
    }


    public boolean isInvested(String ticker){
        for(Investment i : this.investmentList){
            if(i.getTicker().equals(ticker) && i.getNumShares()>0){
                return true;
            }
        }
        return false;
    }


    public Investment getInvestment(String ticker){
        if(isInvested(ticker)) {
            for (Investment i : this.investmentList) {
                if (i.getTicker().equals(ticker)) {
                    return i;
                }
            }
        }else{
        logger.error("Could not find that stock in investments! PLease check that the stock is in the STOCK DB and the ticker is spelled & formatted correctly ");
        }
        return null;
    }


    public void removeInvestment(String ticker){
        for(int i=0; i<this.investmentList.size();i++){
            if(investmentList.get(i).getTicker().equals(ticker)) {
                this.investmentList.remove(i);
                return;
            }
        }
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
