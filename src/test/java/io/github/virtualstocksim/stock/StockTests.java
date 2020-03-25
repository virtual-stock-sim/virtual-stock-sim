package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.SQL;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StockTests
{
    private static final Logger logger = LoggerFactory.getLogger(StockTests.class);

    @Test
    public void testGetId()
    {
        Stock expected = DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA);

        Optional<Stock> stockOptional = Stock.Find(expected.getId());
        assertTrue(stockOptional.isPresent());

        Stock stock = stockOptional.get();

        assertEquals(expected.getId(), stock.getId());
        assertEquals(expected.getSymbol(), stock.getSymbol());
        assertEquals(expected.getCurrPrice().compareTo(stock.getCurrPrice()), 0);
        assertEquals(expected.getPrevClose().compareTo(stock.getPrevClose()), 0);
        assertEquals(expected.getCurrVolume(), stock.getCurrVolume());
        assertEquals(expected.getPrevVolume(), stock.getPrevVolume());
        assertEquals(expected.getStockDataId(), stock.getStockDataId());
    }

    @Test
    public void testGetSymbol()
    {
        Stock expected = DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA);

        Optional<Stock> stockOptional = Stock.Find(expected.getSymbol());
        assertTrue(stockOptional.isPresent());

        Stock stock = stockOptional.get();

        assertEquals(expected.getId(), stock.getId());
        assertEquals(expected.getSymbol(), stock.getSymbol());
        assertEquals(expected.getCurrPrice().compareTo(stock.getCurrPrice()), 0);
        assertEquals(expected.getPrevClose().compareTo(stock.getPrevClose()), 0);
        assertEquals(expected.getCurrVolume(), stock.getCurrVolume());
        assertEquals(expected.getPrevVolume(), stock.getPrevVolume());
        assertEquals(expected.getStockDataId(), stock.getStockDataId());
    }

    @Test
    public void testUpdate()
    {
        BiConsumer<Stock, Stock> testFields = (Stock a, Stock b) ->
        {
            assertEquals(a.getSymbol(),                 b.getSymbol());
            assertEquals(a.getCurrPrice().compareTo(    b.getCurrPrice()), 0);
            assertEquals(a.getPrevClose().compareTo(    b.getPrevClose()), 0);
            assertEquals(a.getCurrVolume(),             b.getCurrVolume());
            assertEquals(a.getPrevVolume(),             b.getPrevVolume());
            assertEquals(a.getStockDataId(),            b.getStockDataId());
        };

        // Original values of stock
        Stock original = DummyStocks.GetDummyStock(DummyStocks.StockSymbol.GOOGLE);
        // What the stock should contain after the commit
        Stock expected = new Stock(-1, "FAKE_SYM", new BigDecimal("12.31"), new BigDecimal("13.10"), 500, 123, 1, SQL.GetTimeStamp());

        // Make sure the stock is retrieved fine
        Optional<Stock> stockOpt = Stock.Find(original.getId());
        assertTrue(stockOpt.isPresent());
        Stock stock = stockOpt.get();

        testFields.accept(original, stock);
        // Set all of the fields
        stock.setSymbol(expected.getSymbol());
        stock.setCurrPrice(expected.getCurrPrice());
        stock.setPrevClose(expected.getPrevClose());
        stock.setCurrVolume(expected.getCurrVolume());
        stock.setPrevVolume(expected.getPrevVolume());
        stock.setStockDataId(expected.getStockDataId());

        try
        {
            stock.update();
        }
        catch (SQLException e)
        {
            logger.error("", e);
        }

        // Get the current information from database
        stockOpt = Stock.Find(original.getId());
        assertTrue(stockOpt.isPresent());
        stock = stockOpt.get();

        // Make sure updated occurred correctly
        testFields.accept(expected, stock);

        // Revert the change
        stock.setSymbol(original.getSymbol());
        stock.setCurrPrice(original.getCurrPrice());
        stock.setPrevClose(original.getPrevClose());
        stock.setCurrVolume(original.getCurrVolume());
        stock.setPrevVolume(original.getPrevVolume());
        stock.setStockDataId(original.getStockDataId());

        try
        {
            stock.update();
        }
        catch (SQLException e)
        {
            logger.error("", e);
        }

        // Get the current information from database
        stockOpt = Stock.Find(original.getId());
        assertTrue(stockOpt.isPresent());
        stock = stockOpt.get();

        // Make sure updated occurred correctly
        testFields.accept(original, stock);
    }
}
