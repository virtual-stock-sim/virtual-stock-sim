package io.github.virtualstocksim.follow;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.following.Follow;
import io.github.virtualstocksim.stock.DummyStocks;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class FollowTest
{



    LinkedList<Follow> dummyFollows = new LinkedList<Follow>();


    public void populate(){

        dummyFollows.add(new Follow(new BigDecimal(100), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.AMAZON), SQL.GetTimeStamp()));
        dummyFollows.add(new Follow(new BigDecimal(498), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA), SQL.GetTimeStamp()));
        dummyFollows.add(new Follow(new BigDecimal(220), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.GOOGLE), SQL.GetTimeStamp()));
        dummyFollows.add(new Follow(new BigDecimal(501), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.FORD), SQL.GetTimeStamp()));
        dummyFollows.add((new Follow(new BigDecimal(.12), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.BDX), SQL.GetTimeStamp())));

    }


    @Test
    public void testGetPercentChange() {
        this.populate();
        assertEquals(0,dummyFollows.get(0).getPercentChange(),0.05);
        assertEquals(149,dummyFollows.get(1).getPercentChange(),0.05);
        assertEquals(-26.67,dummyFollows.get(2).getPercentChange(),0.05);
        assertEquals(25.25,dummyFollows.get(3).getPercentChange(),0.05);
        assertEquals(-99.98,dummyFollows.get(4).getPercentChange(),0.05);

    }
    @Test
    public void testGetCurrentPrice(){
        this.populate();
        assertEquals(dummyFollows.get(0).getCurrentPrice(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.AMAZON).getCurrPrice());
        assertEquals(dummyFollows.get(1).getCurrentPrice(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA).getCurrPrice());
        assertEquals(dummyFollows.get(2).getCurrentPrice(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.GOOGLE).getCurrPrice());
        assertEquals(dummyFollows.get(3).getCurrentPrice(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.FORD).getCurrPrice());
        assertEquals(dummyFollows.get(4).getCurrentPrice(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.BDX).getCurrPrice());
    }

    @Test
    public void testGetInitialPrice(){
        this.populate();
        assertEquals(dummyFollows.get(0).getInitialPrice().doubleValue(),100,0.001);
        assertEquals(dummyFollows.get(1).getInitialPrice().doubleValue(),498.00,0.001);
        assertEquals(dummyFollows.get(2).getInitialPrice().doubleValue(),220.00,0.001);
        assertEquals(dummyFollows.get(3).getInitialPrice().doubleValue(),501,0.001);
        assertEquals(dummyFollows.get(4).getInitialPrice().doubleValue(),.12,0.001);

    }







}
