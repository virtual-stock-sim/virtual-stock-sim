package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.update.StockUpdater;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StockUpdaterTest
{
    private static final Logger logger = LoggerFactory.getLogger(StockUpdaterTest.class);

    @Test
    public void testUpdate() throws IOException
    {
        List<Stock> originalValues = Stock.FindCustom("SELECT * FROM stock");

        for(Stock s : originalValues)
        {
            logger.info(s.getSymbol() + ": " + s.getCurrPrice());
        }

        StockUpdater updater = new StockUpdater();
//        updater.updateNow();
//        updater.stop();

        for(Stock s : Stock.FindCustom("SELECT * FROM stock"))
        {
            logger.info(s.getSymbol() + ": " + s.getCurrPrice());
        }

        // Restore original stock values in test database
        try(Connection conn = StockDatabase.getConnection())
        {
            conn.setAutoCommit(false);
            for(Stock s : originalValues)
            {
                s.update(conn);
            }
            conn.commit();
        }
        catch (SQLException e)
        {
            logger.error("Failed to restore stock values\n", e);
        }
    }

}
