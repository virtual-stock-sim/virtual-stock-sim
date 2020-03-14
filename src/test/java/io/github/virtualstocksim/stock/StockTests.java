package io.github.virtualstocksim.stock;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class StockTests extends StockCacheTestsBase
{
    private static LinkedList<Integer> testStockDataIds;
    private static LinkedList<Integer> testStockIds;

    @BeforeClass
    public static void setup()
    {
        testStockDataIds = new LinkedList<>();
        testStockIds = new LinkedList<>();


        // Populate with some test data
        try
        {
            int numInserts = 4;
            int id;
            id = sc.executeInsert("INSERT INTO stocks_data(data) VALUES('some test data')");
            testStockDataIds.add(id);
            id = sc.executeInsert("INSERT INTO stocks(symbol, curr_price, data_id) VALUES('TEST_SYM_1', 300.43, ?)", id);
            testStockIds.add(id);

            id = sc.executeInsert("INSERT INTO stocks_data(data) VALUES('json string')");
            testStockDataIds.add(id);
            id = sc.executeInsert("INSERT INTO stocks(symbol, curr_price, data_id) VALUES('TEST_SYM_2', 12.58, ?)", id);
            testStockIds.add(id);

            if(testStockDataIds.size() != numInserts/2 && testStockIds.size() != numInserts/2)
            {
                System.err.println("Not all test data was correctly inserted");
                fail();
            }
        } catch (SQLException e)
        {
            sc.logSqlError("Unable to populate test cache with test data", e);
            fail();
        }
    }

    @AfterClass
    public static void cleanup()
    {
        // Remove the test data
        try
        {
            int effected = 0;
            for(int id : testStockIds)
            {
                effected += sc.executeUpdate("DELETE FROM stocks WHERE id = ?", id);
            }
            for(int id : testStockDataIds)
            {
                effected += sc.executeUpdate("DELETE FROM stocks_data WHERE id = ?", id);
            }
            if(effected != testStockDataIds.size() + testStockIds.size())
            {
                System.err.println("Not all test data was correctly deleted");
                fail();
            }
        } catch (SQLException e)
        {
            sc.logSqlError("Not all test data was correctly deleted", e);
            fail();
        }
    }

    @Test
    public void testGetId()
    {
        Stock fromDB = Stock.GetStock(1).get() ;
        Stock expected = new Stock(1, "TSLA", new BigDecimal("360.00"), 1);
        assertEquals(fromDB.getId(), expected.getId());
        assertEquals(fromDB.getSymbol(), expected.getSymbol());
        assertEquals(fromDB.getCurrPrice(), expected.getCurrPrice());
        assertEquals(fromDB.getStockData(), expected.getStockData());
    }

    @Test
    public void testGetSymbol()
    {
        Stock fromDB = Stock.GetStock("TSLA").get() ;
        Stock expected = new Stock(1, "TSLA", new BigDecimal("360.00"), 1);
        assertEquals(fromDB.getId(), expected.getId());
        assertEquals(fromDB.getSymbol(), expected.getSymbol());
        assertEquals(fromDB.getCurrPrice(), expected.getCurrPrice());
        assertEquals(fromDB.getStockData(), expected.getStockData());    }

    @Test
    public void testStockData()
    {
        Stock stock = Stock.GetStock(1).get();
        assertEquals(stock.getStockData(), new StockData(1, "test data 1").getData());
    }

    @Test
    public void testCommit()
    {

    }
}
