package io.github.virtualstocksim.stock;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockUpdaterTest
{
    private static final Logger logger = LoggerFactory.getLogger(StockUpdaterTest.class);

    @Test
    public void testUpdate()
    {
        for(Stock s : Stock.FindCustom("SELECT * FROM stocks"))
        {
            logger.info(s.getSymbol() + ": " + s.getCurrPrice());
        }

        StockUpdater updater = new StockUpdater();
        updater.update();

        for(Stock s : Stock.FindCustom("SELECT * FROM stocks"))
        {
            logger.info(s.getSymbol() + ": " + s.getCurrPrice());
        }
    }

}
