package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.DatabaseConnections;
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
    public static DatabaseConnections databases = new DatabaseConnections();

    @Test
    public void testInstantiation()
    {
        try
        {
            assertTrue(databases.getStockCache().tableExists("stocks_data"));
            assertTrue(databases.getStockCache().tableExists("stocks"));
        } catch (DatabaseException e)
        {
            logger.error("",e);
            fail();
        }

    }

}
