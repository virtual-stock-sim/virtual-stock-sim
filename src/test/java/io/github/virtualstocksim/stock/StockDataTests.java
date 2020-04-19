package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.SQL;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StockDataTests
{
    private static final Logger logger = LoggerFactory.getLogger(StockDataTests.class);

    @Before
    public void resetDB()
    {
        ResetStockDB.reset();
    }

    @Test
    public void testFindById()
    {
        StockData expected = DummyStocks.GetDummyStockData(DummyStocks.StockSymbol.AMAZON);

        StockData data = StockData.Find(expected.getId()).orElse(null);
        assertNotNull(data);
        testFields(expected, data);
    }

    @Test
    public void testUpdate()
    {
        // Original values of stock
        StockData original = DummyStocks.GetDummyStockData(DummyStocks.StockSymbol.GOOGLE);
        // What the stock should contain after the commit
        StockData expected = new StockData(original.getId(), original.getData(), SQL.GetTimeStamp());

        // Make sure the stock is retrieved fine
        StockData data = StockData.Find(original.getId()).orElse(null);
        assertNotNull(data);

        testFields(original, data);
        // Set all of the fields
        data.setData(expected.getData());
        data.setLastUpdated(expected.getLastUpdated());

        try
        {
            data.update();
        }
        catch (SQLException e)
        {
            logger.error("", e);
        }

        // Get the current information from database
        data = StockData.Find(original.getId()).orElse(null);
        assertNotNull(data);

        // Make sure updated occurred correctly
        testFields(expected, data);

        // Revert the change
        data.setData(original.getData());
        data.setLastUpdated(original.getLastUpdated());

        try
        {
            data.update();
        }
        catch (SQLException e)
        {
            logger.error("", e);
        }

        // Get the current information from database
        data = StockData.Find(original.getId()).orElse(null);
        assertNotNull(data);

        // Make sure updated occurred correctly
        testFields(original, data);
    }

    public void testFields(StockData a, StockData b)
    {
        assertEquals(a.getId(),             b.getId());
        assertEquals(a.getData(),           b.getData());
        assertEquals(a.getLastUpdated(),    b.getLastUpdated());
    }
}
