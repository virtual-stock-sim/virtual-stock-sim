package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.scraper.TimeInterval;
import io.github.virtualstocksim.update.StockUpdater;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertNotEquals;

public class StockUpdaterTest
{
    private static final Logger logger = LoggerFactory.getLogger(StockUpdaterTest.class);

    @Before
    public void resetDB()
    {
        ResetStockDB.reset();
    }

    @Test
    public void testStockUpdate()
    {
        List<Stock> original = Stock.FindAll();

        List<Stock> updated = Stock.FindAll();

        StockUpdater.updateStocks(updated);

        // Make sure the function updated the objects correctly
        for(int i = 0; i < updated.size(); ++i)
        {
            testStockFields(original.get(i), updated.get(i));
        }

        // Make sure the info was commited to the db correctly
        updated = Stock.FindAll();
        for(int i = 0; i < updated.size(); ++i)
        {
            testStockFields(original.get(i), updated.get(i));
        }
    }

    @Test
    public void testStockDataUpdate()
    {
        List<StockData> original = StockData.FindAll();
        StockUpdater.updateStockDatas(Stock.FindAll(), TimeInterval.THREEMONTH);

        List<StockData> updated = StockData.FindAll();

        // Make sure the original and in-db info is different
        for(int i = 0; i < updated.size(); ++i)
        {
            testStockDataFields(original.get(i), updated.get(i));
        }
    }

    public void testStockFields(Stock a, Stock b)
    {
        assertNotEquals(a.getCurrPrice().compareTo(    b.getCurrPrice()), 0);
        assertNotEquals(a.getPrevClose().compareTo(    b.getPrevClose()), 0);
        assertNotEquals(a.getCurrVolume(),             b.getCurrVolume());
        /*
         * Current way of getting new data doesn't have a way to get previous volume
         */
        //assertNotEquals(a.getPrevVolume(),             b.getPrevVolume());
        assertNotEquals(a.getLastUpdated(),            b.getLastUpdated());
    }

    public void testStockDataFields(StockData a, StockData b)
    {
        assertNotEquals(a.getData(), b.getData());
        assertNotEquals(a.getLastUpdated(), b.getLastUpdated());
    }

}
