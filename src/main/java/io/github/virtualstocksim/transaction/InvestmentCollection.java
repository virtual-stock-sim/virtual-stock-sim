package io.github.virtualstocksim.transaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.virtualstocksim.stock.Stock;
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

    public void updateInvestments(String s ){
        this.investmentList = this.stringToInvestmentList(s);
    }

    //if for the leaderboard we want to do percent change for each user
    //So we will need to add fields here for the cash they put in FOR EACH investment (sum of), as well as the current value of all the holdings in their acct (stock not cash on hand)

    //upon adding investment, check if already exists and add to numshares, otherwise add new share
    public void addInvestment(Investment in ) {
        if(isInvested(in.getSymbol())) {
            for (Investment i : investmentList) {
                //look through all of the symbols, if there is a match just update the number of shares of that stock
                if (in.getSymbol().equals(i.getSymbol())) {
                    i.setNumShares(i.getNumShares() + in.getNumShares());
                    break;
                }
            }
        }else {
            investmentList.add(in);
        }
    }


    public boolean isInvested(String symbol){
        for(Investment i : this.investmentList){
            if(i.getSymbol().equals(symbol) && i.getNumShares()>0){
                return true;
            }
        }
        return false;
    }


    public Investment getInvestment(String symbol){
        if(isInvested(symbol)) {
            for (Investment i : this.investmentList) {
                if (i.getSymbol().equals(symbol)) {
                    return i;
                }
            }
        }else{
        logger.error("Could not find that stock in investments! PLease check that the stock is in the STOCK DB and the symbol is spelled & formatted correctly ");
        }
        return null;
    }


    public void removeInvestment(String symbol){
        for(int i=0; i<this.investmentList.size();i++){
            if(investmentList.get(i).getSymbol().equals(symbol)) {
                this.investmentList.remove(i);
                return;
            }
        }
    }

    public String buildJSON(){

        JsonArray ja = new JsonArray();
        for(Investment i  : this.investmentList){
            JsonObject jo = new JsonObject();                               //Shares, Symbol, Timestamp
            jo.addProperty("shares",i.getNumShares());
            jo.addProperty("symbol",i.getSymbol());
            jo.addProperty("timestamp",i.getTimestamp().toString());
            ja.add(jo);
        }
        return ja.toString();
    }

    public List<Investment> stringToInvestmentList(String s ){
         List<Investment> temp = new LinkedList<>();
         if(s ==null || s.trim().isEmpty()){
             return temp;
         }else{

        JsonElement elem = JsonParser.parseString(s);
        if(elem.isJsonArray())
        {
            JsonArray j = elem.getAsJsonArray();
            for(JsonElement x : j ){
                //this might be dangerous if a stock is somehow removed from the DB while iterating processing
                Stock tempStock = Stock.Find(x.getAsJsonObject().get("symbol").getAsString()).orElseGet(null);
                temp.add(new Investment(x.getAsJsonObject().get("shares").getAsInt(),tempStock, Timestamp.valueOf(x.getAsJsonObject().get("timestamp").getAsString())));
            }

        }

        return temp;
        }
    }
}
