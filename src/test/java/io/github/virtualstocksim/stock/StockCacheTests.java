package io.github.virtualstocksim.stock;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class StockCacheTests
{
    private static StockCache sc;

    @BeforeClass
    public static void setup()
    {
        sc = StockCache.Instance();
    }

    @Test
    public void testSetup()
    {
        try
        {
            assertTrue(sc.tableExists("stocks_data"));
            assertTrue(sc.tableExists("stocks"));
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
            fail();
        }

    }

}
