package io.github.virtualstocksim.follow;
import io.github.virtualstocksim.stock.DummyStocks;

import io.github.virtualstocksim.following.Follow;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.util.Util;
import org.junit.ClassRule;
import org.junit.Test;
import io.github.virtualstocksim.stock.DummyStocks;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class FollowTest
{


    Follow [] testFollowList = new Follow[5];
    public void populate(){
        testFollowList[0]=(new Follow(new BigDecimal(100), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.AMAZON), Util.GetTimeStamp()));
        testFollowList[1]=(new Follow(new BigDecimal(498), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA), Util.GetTimeStamp()));
        testFollowList[2]=(new Follow(new BigDecimal(220), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.GOOGLE), Util.GetTimeStamp()));
        testFollowList[3]=(new Follow(new BigDecimal(501), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.FORD), Util.GetTimeStamp()));
        testFollowList[4]=(new Follow(new BigDecimal(.12), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.APPLE), Util.GetTimeStamp()));
    }
    @Test
    public void testGetPercentChange() {
        this.populate();
        assertEquals(0,testFollowList[0].getPercentChange(),0.05);
        assertEquals(149,testFollowList[1].getPercentChange(),0.05);
        assertEquals(-26.67,testFollowList[2].getPercentChange(),0.05);
        assertEquals(25.25,testFollowList[3].getPercentChange(),0.05);
        assertEquals(-99.98,testFollowList[4].getPercentChange(),0.05);

    }
    @Test
    public void testGetCurrentPrice(){
        this.populate();
        assertEquals(testFollowList[0].getCurrentPrice(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.AMAZON).getCurrPrice());
        assertEquals(testFollowList[1].getCurrentPrice(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA).getCurrPrice());
        assertEquals(testFollowList[2].getCurrentPrice(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.GOOGLE).getCurrPrice());
        assertEquals(testFollowList[3].getCurrentPrice(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.FORD).getCurrPrice());
        assertEquals(testFollowList[4].getCurrentPrice(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.APPLE).getCurrPrice());
    }

    @Test
    public void testGetInitialPrice(){
        this.populate();
        assertEquals(testFollowList[0].getInitialPrice().doubleValue(),100,0.001);
        assertEquals(testFollowList[1].getInitialPrice().doubleValue(),498.00,0.001);
        assertEquals(testFollowList[2].getInitialPrice().doubleValue(),220.00,0.001);
        assertEquals(testFollowList[3].getInitialPrice().doubleValue(),501,0.001);
        assertEquals(testFollowList[4].getInitialPrice().doubleValue(),.12,0.001);

    }



}
