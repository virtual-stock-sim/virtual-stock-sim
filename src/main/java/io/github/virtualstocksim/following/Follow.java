package io.github.virtualstocksim.following;
import io.github.virtualstocksim.stock.Stock;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Follow {
    private BigDecimal  initialPrice,currentPrice;
    private Stock stock;
    private double percentChange;

    //constructor !!!s
    public Follow(BigDecimal initialPrice, Stock stock){
        this.stock=stock;
        this.initialPrice=initialPrice;
        this.currentPrice=stock.getCurrPrice();
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
    //Stock Contains a current price
    public void setCurrentPrice(){
        this.currentPrice=this.stock.getCurrPrice();
    }
    public double getPercentChange(){
        percentChange=this.getCurrentPrice().doubleValue()-this.getInitialPrice().doubleValue();
        percentChange=(percentChange/Math.abs(currentPrice.doubleValue()))*-1;

        return percentChange*100;
    }
}
