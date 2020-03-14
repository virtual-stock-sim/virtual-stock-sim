package io.github.virtualstocksim.stock;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class StockCacheTests extends StockCacheTestsBase
{
    @BeforeClass
    public static void setup()
    {

    }

    @Test
    public void testInstantiation()
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
