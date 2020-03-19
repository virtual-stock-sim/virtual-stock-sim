package io.github.virtualstocksim.database;

import io.github.virtualstocksim.account.AccountDatabase;
import io.github.virtualstocksim.stock.DummyStocks;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.stock.StockCache;
import io.github.virtualstocksim.stock.StockData;
import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.fail;


public class DatabaseConnections extends ExternalResource
{
    public static final Logger logger = LoggerFactory.getLogger(DatabaseConnections.class);

    public final String testFolderPath = "testTemp";
    public final String genericDBPath = testFolderPath + String.format("/test_generic_database%s.db", UUID.randomUUID().toString());
    public final String accountDBPath = testFolderPath + String.format("/test_account_database%s.db", UUID.randomUUID().toString());
    public final String stockCacheDBPath = testFolderPath + String.format("/test_stockcache_database%s.db", UUID.randomUUID().toString());

    protected Database genericDB = null;
    protected AccountDatabase accDB = AccountDatabase.Instance();
    protected StockCache stockCache = StockCache.Instance();

    public Database getGenericDB()
    {
        return genericDB;
    }

    public AccountDatabase getAccDB()
    {
        return accDB;
    }

    public StockCache getStockCache()
    {
        return stockCache;
    }

    @Override
    protected void before() throws Throwable
    {
        try
        {
            genericDB = new Database(genericDBPath);
            accDB.changeDB(accountDBPath);
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
            if(genericDB != null) genericDB.closeConn();
            accDB.closeConn();
            stockCache.closeConn();
        }
        catch (DatabaseException e)
        {
            logger.error("",e);
            fail();
        }

        try
        {
            DriverManager.getConnection(String.format("jdbc:derby:%s;shutdown=true", genericDBPath));
        }
        catch (SQLException e) {}
        try
        {
            DriverManager.getConnection(String.format("jdbc:derby:%s;shutdown=true", accountDBPath));
        }
        catch (SQLException e) {}
        try
        {
            DriverManager.getConnection(String.format("jdbc:derby:%s;shutdown=true", stockCacheDBPath));
        }
        catch (SQLException e) {}

        deleteTempFolder();
    }

    private void deleteTempFolder()
    {
        File file = new File(testFolderPath);
        if(file.exists())
        {
            try
            {
                FileUtils.deleteDirectory(file);
            } catch (IOException e)
            {
                logger.error(String.format("Couldn't delete temporary test folder%s\n", e.getMessage()));
            }
        }
    }
}
