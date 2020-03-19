package io.github.virtualstocksim.stock;

import org.junit.ClassRule;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StockDataTests
{
    @ClassRule
    public static StockCacheConnection conn = new StockCacheConnection();

    @Test
    public void testGetId()
    {
        StockData expected = DummyStocks.GetDummyStockData(DummyStocks.StockSymbol.AMAZON);

        Optional<StockData> dataOptional = StockData.Find(expected.getId());
        assertTrue(dataOptional.isPresent());
        StockData stockData = dataOptional.get();

        assertEquals(stockData.getId(), expected.getId());
        assertTrue(stockData.getData().equals(expected.getData()));
    }

/*    @Test
    public void testCommit()
    {

    }*/
}
