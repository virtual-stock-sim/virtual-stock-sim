package io.github.virtualstocksim.transaction;

import io.github.virtualstocksim.stock.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Investment {

    private int numShares;
    private BigDecimal pricePerShare,totalHoldings;
    private Timestamp timeStamp;
    private String symbol;

    private static final Logger logger = LoggerFactory.getLogger(Investment.class);
    //The price will change DYNAMICALLY!!! from the DB there is no need to store that information in some local variables unless the need for calculations arise at a later time
    //This is the real difference between this class and transaction, really
    public Investment(int numShares, String symbol, Timestamp timestamp){
            this.symbol=symbol;
            this.timeStamp=timestamp;
            this.numShares=numShares;
            this.pricePerShare= Stock.Find(symbol).get().getCurrPrice();
            this.totalHoldings = this.pricePerShare.multiply(new BigDecimal(numShares));
    }

    public BigDecimal getTotalHoldings(){
        Stock tempStock = Stock.Find(symbol).orElseGet(null);
        if(tempStock == null){
            logger.error("No stock in the database for that investment!");
        }
       return tempStock.getCurrPrice().multiply(new BigDecimal(this.getNumShares()));
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
    public String getSymbol(){
        return this.symbol;
    }
    public int getNumShares(){
        return this.numShares;
    }
    public Timestamp getTimestamp(){
        return this.timeStamp;
    }

    public void setNumShares(int input) {
        if (input < 0) {
            logger.error("Error: The user cannot hold less than 0 shares in a company");
        } else {
            this.numShares = input;
        }
    }

}
