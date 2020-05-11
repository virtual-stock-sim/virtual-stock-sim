package io.github.virtualstocksim.follow;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.following.FollowedStock;
import io.github.virtualstocksim.stock.DummyStocks;
import io.github.virtualstocksim.stock.Stock;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FollowedStockTest
{
    private static final Logger logger = LoggerFactory.getLogger(FollowedStockTest.class);
    private static final Timestamp testTimestamp = SQL.GetTimeStamp();

    public List<FollowedStock> getTestFollowedStocks()
    {
        List<FollowedStock> followedStocks = new LinkedList<>();
        for(Stock s : DummyStocks.GetDummyStocks().values())
        {
            followedStocks.add(new FollowedStock(s, s.getCurrPrice(), testTimestamp));
        }
        return followedStocks;
    }
    @Test
    public void testGetPercentChange()
    {
        List<FollowedStock> testFollowedStocks = getTestFollowedStocks();
        Iterator<FollowedStock> followedIt = testFollowedStocks.iterator();
        Iterator<Stock> dummyIt = DummyStocks.GetDummyStocks().values().iterator();

        while(followedIt.hasNext())
        {
            Stock dummy = dummyIt.next();
            FollowedStock followed = followedIt.next();
            // Make a new stock so that the dummy stock isn't being edited
            Stock stock = new Stock(followed.getStock().getSymbol(), followed.getStock().getCurrPrice(), new BigDecimal("0.0"), 0, 0);
            stock.setCurrPrice(stock.getCurrPrice().subtract(BigDecimal.TEN));

            double expectedPerChange = percentChange(dummy.getCurrPrice(), followed.getInitialPrice());
            assertEquals(expectedPerChange, followed.getPercentChange(), 0.0);
        }
    }

    @Test
    public void testGetInitialPrice()
    {
        List<FollowedStock> testFollowedStocks = getTestFollowedStocks();
        Iterator<FollowedStock> followedIt = testFollowedStocks.iterator();
        Iterator<Stock> dummyIt = DummyStocks.GetDummyStocks().values().iterator();

        while(followedIt.hasNext())
        {
            assertEquals(dummyIt.next().getCurrPrice(), followedIt.next().getInitialPrice());
        }
    }

    private static final BigDecimal DECIMAL_100 = new BigDecimal("100.0");
    public double percentChange(BigDecimal original, BigDecimal current)
    {
        if(current != null && original != null)
        {
            if(original.compareTo(BigDecimal.ZERO) == 0)
            {
                return 100.0;
            }
            else
            {
                BigDecimal diff = current.subtract(original, MathContext.DECIMAL64);
                BigDecimal change = diff.divide(original.abs(), 9, RoundingMode.HALF_EVEN);
                BigDecimal percentChange = change.multiply(DECIMAL_100);

                DecimalFormat df = new DecimalFormat("#.##");
                return Double.parseDouble(df.format(percentChange));
            }
        }
        else
        {
            throw new NullPointerException("Current price and/or previous closing prices are null");
        }
    }




}
