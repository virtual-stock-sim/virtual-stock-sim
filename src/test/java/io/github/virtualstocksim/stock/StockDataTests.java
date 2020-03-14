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
        StockData fromDB = StockData.GetStockData(1).get();
        StockData expected = new StockData(1, "test data 1");
        assertEquals(fromDB.getId(), expected.getId());
        assertEquals(fromDB.getData(), expected.getData());
    }

    @Test
    public void testCommit()
    {

    }
}
