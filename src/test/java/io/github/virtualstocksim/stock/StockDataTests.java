package io.github.virtualstocksim.stock;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class StockDataTests extends StockCacheTestsBase
{
    @BeforeClass
    public static void setup()
    {

    }

    @Test
    public void testGetId()
    {
        assertEquals(StockData.GetStockData(1).get(), new StockData(1, "test data 1"));
    }

    @Test
    public void testCommit()
    {

    }
}
