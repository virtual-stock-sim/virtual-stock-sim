package io.github.virtualstocksim.following;

import io.github.virtualstocksim.stock.Stock;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;

public class Follow {
    private static final Logger logger = LoggerFactory.getLogger(Follow.class);

    private BigDecimal  initialPrice,currentPrice;
    private Stock stock;
    private double percentChange;
    private Timestamp timeStamp;

    public Follow(BigDecimal initialPrice, Stock stock, Timestamp timeStamp){
        this.stock = stock;
        this.initialPrice = initialPrice;
        this.currentPrice = stock.getCurrPrice();
        this.timeStamp = timeStamp;
        //only need store initial price (@ time of follow)in here
        //current price will always just be gotten from stock
    }

    public Stock getStock(){
        return this.stock;
    }


    public BigDecimal getCurrentPrice(){
        return this.stock.getCurrPrice();
    }


    public BigDecimal getInitialPrice(){
        return this.initialPrice;
    }


    public Timestamp getTimeStamp(){
        return this.timeStamp;
    }


    private static final BigDecimal DECIMAL_100 = new BigDecimal("100.0");
    public double getPercentChange() {
        if(stock.getCurrPrice() != null && initialPrice != null)
        {
            BigDecimal diff = initialPrice.subtract(stock.getCurrPrice(), MathContext.DECIMAL64);
            BigDecimal change = diff.divide(stock.getCurrPrice().abs(), 9, RoundingMode.HALF_EVEN);
            BigDecimal percentChange = change.multiply(DECIMAL_100);

            DecimalFormat df = new DecimalFormat("#.##");
            return Double.parseDouble(df.format(percentChange));
        }
        else
        {
            throw new NullPointerException("Current price and/or initial prices are null");
        }
    }
}
