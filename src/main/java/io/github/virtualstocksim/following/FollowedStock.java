package io.github.virtualstocksim.following;

import com.google.gson.JsonObject;
import io.github.virtualstocksim.stock.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;

/**
 * Represents a stock that is being followed by an account
 */
public class FollowedStock
{
    private static final Logger logger = LoggerFactory.getLogger(FollowedStock.class);

    private BigDecimal initialPrice;
    private Stock stock;
    private Timestamp timestamp;

    public FollowedStock(Stock stock, BigDecimal initalialPrice, Timestamp timestamp)
    {
        this.initialPrice = stock.getCurrPrice();
        this.stock = stock;
        this.timestamp = timestamp;
    }

    public BigDecimal getInitialPrice()
    {
        return initialPrice;
    }

    public void setInitialPrice(BigDecimal initialPrice)
    {
        this.initialPrice = initialPrice;
    }

    public Stock getStock()
    {
        return stock;
    }

    public void setStock(Stock stock)
    {
        this.stock = stock;
    }

    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }

    public JsonObject asJsonObj()
    {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("symbol", stock.getSymbol());
        jsonObj.addProperty("initialPrice", initialPrice);
        jsonObj.addProperty("timestamp", String.valueOf(timestamp));
        return jsonObj;
    }

    private static final BigDecimal DECIMAL_100 = new BigDecimal("100.0");
    public double getPercentChange()
    {
        BigDecimal currPrice = stock.getCurrPrice();
        if(currPrice != null && initialPrice != null)
        {
            if(currPrice.compareTo(BigDecimal.ZERO) == 0)
            {
                return 100.0;
            }
            else
            {
                BigDecimal diff = initialPrice.subtract(currPrice, MathContext.DECIMAL64);
                BigDecimal change = diff.divide(currPrice.abs(), 9, RoundingMode.HALF_EVEN);
                BigDecimal percentChange = change.multiply(DECIMAL_100);

                DecimalFormat df = new DecimalFormat("#.##");
                return Double.parseDouble(df.format(percentChange));
            }

        }
        else
        {
            throw new NullPointerException("Current price and/or initial prices are null");
        }
    }
}