package io.github.virtualstocksim.stock;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StockDataTests
{
    @Test
    public void testGetId()
    {
        StockData expected = DummyStocks.GetDummyStockData(DummyStocks.StockSymbol.AMAZON);

        Optional<StockData> dataOptional = StockData.Find(expected.getId());
        assertTrue(dataOptional.isPresent());
        StockData stockData = dataOptional.get();

        assertEquals(stockData.getId(), expected.getId());
        assertEquals(stockData.getData(), expected.getData());
    }

/*    @Test
    public void testCommit()
    {

    }*/
}
