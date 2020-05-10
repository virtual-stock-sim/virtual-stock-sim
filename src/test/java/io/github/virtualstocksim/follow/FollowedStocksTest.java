package io.github.virtualstocksim.follow;

import com.google.gson.JsonArray;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.following.FollowedStock;
import io.github.virtualstocksim.following.FollowedStocks;
import io.github.virtualstocksim.stock.DummyStocks;
import io.github.virtualstocksim.stock.DummyStocks.StockSymbol;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class FollowedStocksTest
{
    private static final Timestamp testTimestamp = SQL.GetTimeStamp();
    private static final Map<String, FollowedStock> testMap = new HashMap<>();
    private static final JsonArray testJson = new JsonArray();

    static
    {
        testMap.put(StockSymbol.AMAZON.getSymbol(), new FollowedStock(DummyStocks.GetDummyStock(StockSymbol.AMAZON), new BigDecimal("100.0"), testTimestamp));
        testMap.put(StockSymbol.TESLA.getSymbol(), new FollowedStock(DummyStocks.GetDummyStock(StockSymbol.TESLA), new BigDecimal("498.0"), testTimestamp));
        testMap.put(StockSymbol.GOOGLE.getSymbol(), new FollowedStock(DummyStocks.GetDummyStock(StockSymbol.GOOGLE), new BigDecimal("220.0"), testTimestamp));
        testMap.put(StockSymbol.FORD.getSymbol(), new FollowedStock(DummyStocks.GetDummyStock(StockSymbol.FORD), new BigDecimal("501.0"), testTimestamp));
        testMap.put(StockSymbol.BDX.getSymbol(), new FollowedStock(DummyStocks.GetDummyStock(StockSymbol.BDX), new BigDecimal("0.12"), testTimestamp));

        for(FollowedStock followedStock : testMap.values())
        {
            testJson.add(followedStock.asJsonObj());
        }
    }

    @Test
    public void testParseJson()
    {
        FollowedStocks followedStocks = new FollowedStocks(String.valueOf(testJson));

        for(String stockSymbol : testMap.keySet())
        {
            assertTrue(followedStocks.contains(stockSymbol));
        }
    }

    @Test
    public void testAddFollowedStock()
    {
        FollowedStocks followedStocks = new FollowedStocks(new HashMap<>());

        for(FollowedStock followedStock : testMap.values())
        {
            followedStocks.addFollowedStock(followedStock);
        }

        for(String stockSymbol : testMap.keySet())
        {
            assertTrue(followedStocks.contains(stockSymbol));
        }
    }

    @Test
    public void testRemoveFollowedStock()
    {
        FollowedStocks followedStocks = new FollowedStocks(new HashMap<>());

        for(FollowedStock followedStock : testMap.values())
        {
            followedStocks.addFollowedStock(followedStock);
        }

        for(String stockSymbol : testMap.keySet())
        {
            assertTrue(followedStocks.contains(stockSymbol));
        }

        for(FollowedStock followedStock : testMap.values())
        {
            followedStocks.removeFollowedStock(followedStock.getStock().getSymbol());
        }

        for(String stockSymbol : testMap.keySet())
        {
            assertFalse(followedStocks.contains(stockSymbol));
        }
    }

}
