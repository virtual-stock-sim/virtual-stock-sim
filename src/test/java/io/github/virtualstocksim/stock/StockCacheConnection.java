package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.DatabaseException;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.fail;

public class StockCacheConnection extends ExternalResource
{
    private static final Logger logger = LoggerFactory.getLogger(StockCacheConnection.class);
    public final String stockCacheDBPath = String.format("testdbs/test_stockcache_database%s.db", UUID.randomUUID().toString());

    private StockCache stockCache = StockCache.Instance();
    public StockCache getStockCache()
    {
        return stockCache;
    }

    @Override
    protected void before() throws Throwable
    {
        try
        {
            stockCache.changeDB(stockCacheDBPath);

            for(StockData s : DummyStocks.GetDummyStockDatas().values())
            {
                stockCache.executeInsert("INSERT INTO stocks_data(data) VALUES(?)", s.getData());
            }

            for(Stock s : DummyStocks.GetDummyStocks().values())
            {
                stockCache.executeInsert("INSERT INTO stocks(symbol, curr_price, data_id) VALUES(?, ?, ?)", s.getSymbol(), s.getCurrPrice(), s.getStockData().getId());
            }
        }
        catch (DatabaseException e)
        {
            logger.error("",e);
            fail();
        }
    }

    @Override
    protected void after()
    {
        try
        {
            stockCache.closeConn();
        }
        catch (DatabaseException e)
        {
            logger.error("",e);
            fail();
        }

        try
        {
            DriverManager.getConnection(String.format("jdbc:derby:%s;shutdown=true", stockCacheDBPath));
        }
        catch (SQLException e) {}

        File db = new File(stockCacheDBPath);
        if(db.exists())
        {
            db.delete();
        }
    }
}
