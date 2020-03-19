package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.DatabaseConnections;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StockTests
{
    @ClassRule
    public static DatabaseConnections databases = new DatabaseConnections();

    @Test
    public void testGetId()
    {
        Stock expected = DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA);

        Optional<Stock> stockOptional = Stock.Find(expected.getId());
        assertTrue(stockOptional.isPresent());

        Stock stock = stockOptional.get();

        assertEquals(stock.getId(), expected.getId());
        assertEquals(stock.getSymbol(), expected.getSymbol());
        assertEquals(stock.getCurrPrice().compareTo(expected.getCurrPrice()), 0);
        assertEquals(stock.getStockData().getId(), expected.getStockData().getId());
    }

    @Test
    public void testGetSymbol()
    {
        Stock expected = DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA);

        Optional<Stock> stockOptional = Stock.Find(expected.getSymbol());
        assertTrue(stockOptional.isPresent());

        Stock stock = stockOptional.get();

        assertEquals(stock.getId(), expected.getId());
        assertTrue(stock.getSymbol().equals(expected.getSymbol()));
        assertEquals(stock.getCurrPrice().compareTo(expected.getCurrPrice()), 0);
        assertEquals(stock.getStockData().getId(), expected.getStockData().getId());
    }

/*    @Test
    public void testCommit()
    {

    }*/
}
