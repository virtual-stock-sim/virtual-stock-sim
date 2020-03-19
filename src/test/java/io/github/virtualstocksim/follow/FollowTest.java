package io.github.virtualstocksim.follow;
import io.github.virtualstocksim.stock.DummyStocks;
import io.github.virtualstocksim.database.DatabaseConnections;
import io.github.virtualstocksim.following.Follow;
import io.github.virtualstocksim.stock.Stock;
import org.junit.ClassRule;
import org.junit.Test;
import io.github.virtualstocksim.stock.DummyStocks;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class FollowTest extends DatabaseConnections
{
    @ClassRule
    public static DatabaseConnections databases = new DatabaseConnections();

    Follow [] testFollowList = new Follow[5];
    public void populate(){
        testFollowList[0]=(new Follow(new BigDecimal(100), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA)));
        testFollowList[1]=(new Follow(new BigDecimal(498), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA)));
        testFollowList[2]=(new Follow(new BigDecimal(320), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA)));
        testFollowList[3]=(new Follow(new BigDecimal(50),   DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA)));
        testFollowList[4]=(new Follow(new BigDecimal(.12), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA)));
    }
    @Test
    public void testGetPercentChange() {
        this.populate();
        assertEquals(0,testFollowList[0].getPercentChange(),0.05);
        assertEquals(2829.41,testFollowList[1].getPercentChange(),0.05);
        assertEquals(160.16,testFollowList[2].getPercentChange(),0.05);
        assertEquals(-98.75,testFollowList[3].getPercentChange(),0.05);
        assertEquals(-99.76,testFollowList[4].getPercentChange(),0.05);

    }
    @Test
    public void testGetCurrentPrice(){
        this.populate();
        assertEquals(testFollowList[0].getCurrentPrice(),Stock.Find(1).get().getCurrPrice());
        assertEquals(testFollowList[1].getCurrentPrice(),Stock.Find(2).get().getCurrPrice());
        assertEquals(testFollowList[2].getCurrentPrice(),Stock.Find(3).get().getCurrPrice());
        assertEquals(testFollowList[3].getCurrentPrice(),Stock.Find(4).get().getCurrPrice());
        assertEquals(testFollowList[4].getCurrentPrice(),Stock.Find(5).get().getCurrPrice());

    }

    @Test
    public void testGetInitialPrice(){
        this.populate();
        assertEquals(testFollowList[0].getInitialPrice().doubleValue(),100,0.001);
        assertEquals(testFollowList[1].getInitialPrice().doubleValue(),498.00,0.001);
        assertEquals(testFollowList[2].getInitialPrice().doubleValue(),320.00,0.001);
        assertEquals(testFollowList[3].getInitialPrice().doubleValue(),5.00,0.001);
        assertEquals(testFollowList[4].getInitialPrice().doubleValue(),.12,0.001);

    }



}
