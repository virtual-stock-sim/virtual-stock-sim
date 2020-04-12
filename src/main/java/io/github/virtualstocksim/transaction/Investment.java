package io.github.virtualstocksim.transaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.virtualstocksim.stock.Stock;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedList;

public class Investment {
    private int numShares;
    private BigDecimal pricePerShare,totalHoldings;
    private Timestamp timeStamp;
    private String ticker;


    //The price will change DYNAMICALLY!!! there is no need to store that information in some local variables unless the need for calculations arise at a later time
    //think about the difference between this class and transaction
    public Investment(int numShares, String ticker, Timestamp timestamp){
            this.numShares=numShares;
            this.pricePerShare= Stock.Find(ticker).get().getCurrPrice();
            this.totalHoldings = this.pricePerShare.multiply(new BigDecimal(numShares));
    }

    public BigDecimal getTotalHoldings(){
       return this.totalHoldings;
    }
    public int numShares(){
        return this.numShares();
    }
    public BigDecimal getPricePerShare(){
        return this.getPricePerShare();
    }
    public Timestamp timestamp(){
        return this.timestamp();
    }
    public String getTicker(){
        return this.ticker;
    }
    public int getNumShares(){
        return this.numShares;
    }
    public Timestamp getTimestamp(){
        return this.timeStamp;
    }

    public void setNumShares(int input){this.numShares=input;}

}
