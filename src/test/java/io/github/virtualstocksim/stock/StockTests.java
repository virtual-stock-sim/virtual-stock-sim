package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.SQL;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StockTests
{
    private static final Logger logger = LoggerFactory.getLogger(StockTests.class);

    @Before
    public void resetDB()
    {
        ResetStockDB.reset();
    }

    @Test
    public void testFindById()
    {
        Stock expected = DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA);

        Stock stock = Stock.Find(expected.getId()).orElse(null);
        assertNotNull(stock);

        testFields(expected, stock);
    }

    @Test
    public void testFindBySymbol()
    {
        Stock expected = DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA);

        Stock stock = Stock.Find(expected.getSymbol()).orElse(null);
        assertNotNull(stock);

        testFields(expected, stock);
    }

    @Test
    public void testUpdate()
    {
        // Original values of stock
        Stock original = DummyStocks.GetDummyStock(DummyStocks.StockSymbol.GOOGLE);
        // What the stock should contain after the commit
        Stock expected = new Stock(original.getId(), "FAKE_SYM", new BigDecimal("12.31"), new BigDecimal("13.10"), 500, 123, 1, SQL.GetTimeStamp());

        // Make sure the stock is retrieved fine
        Stock stock = Stock.Find(original.getId()).orElse(null);
        assertNotNull(stock);

        testFields(original, stock);
        // Set all of the fields
        stock.setSymbol(expected.getSymbol());
        stock.setCurrPrice(expected.getCurrPrice());
        stock.setPrevClose(expected.getPrevClose());
        stock.setCurrVolume(expected.getCurrVolume());
        stock.setPrevVolume(expected.getPrevVolume());
        stock.setStockDataId(expected.getStockDataId());
        stock.setLastUpdated(expected.getLastUpdated());

        try
        {
            stock.update();
        }
        catch (SQLException e)
        {
            logger.error("", e);
        }

        // Get the current information from database
        stock = Stock.Find(original.getId()).orElse(null);
        assertNotNull(stock);

        // Make sure updated occurred correctly
        testFields(expected, stock);
    }

    public void testFields(Stock a, Stock b)
    {
        assertEquals(a.getId(),                     b.getId());
        assertEquals(a.getSymbol(),                 b.getSymbol());
        assertEquals(a.getCurrPrice().compareTo(    b.getCurrPrice()), 0);
        assertEquals(a.getPrevClose().compareTo(    b.getPrevClose()), 0);
        assertEquals(a.getCurrVolume(),             b.getCurrVolume());
        assertEquals(a.getPrevVolume(),             b.getPrevVolume());
        assertEquals(a.getStockDataId(),            b.getStockDataId());
        assertEquals(a.getLastUpdated(),            b.getLastUpdated());
    }
}
