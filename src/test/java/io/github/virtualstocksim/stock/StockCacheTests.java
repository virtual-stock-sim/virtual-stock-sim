package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.DatabaseException;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StockCacheTests
{
    private static final Logger logger = LoggerFactory.getLogger(StockCacheTests.class);

    @ClassRule
    public static StockCacheConnection conn = new StockCacheConnection();

    @Test
    public void testInstantiation()
    {
        try
        {
            assertTrue(conn.getStockCache().tableExists("stocks_data"));
            assertTrue(conn.getStockCache().tableExists("stocks"));
        } catch (DatabaseException e)
        {
            logger.error("",e);
            fail();
        }

    }

}
