package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.Database;
import io.github.virtualstocksim.database.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Controller for stock data cache
public class StockCache extends Database
{
    private static final Logger logger = LoggerFactory.getLogger(StockCache.class);
    private static final String initDBPath = "vss_stockcache.db";

    private static StockCache singleton = init();
    public static StockCache Instance() { return singleton; }

    private static StockCache init()
    {
        try
        {
            return new StockCache(initDBPath);
        } catch (DatabaseException e)
        {
            logger.error(String.format("Unable to open connection to stock cache database\n%s", e));
            System.exit(-1);
        }
        return null;
    }

    private StockCache(String dbPath) throws DatabaseException
    {
        super(dbPath, logger);
        try
        {
            createTables();
        } catch (DatabaseException e)
        {
            logger.error(String.format("Unable to create tables for stock cache\n%s", e));
            System.exit(-1);
        }
    }

    // Create tables if they don't exist
    private void createTables() throws DatabaseException
    {
        if(!tableExists("stocks_data"))
        {
            createTable("stocks_data",
                    "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)",
                    "data LONG VARCHAR NOT NULL",
                    "last_updated TIMESTAMP"
                    );
        }

        if(!tableExists("stocks"))
        {
            createTable("stocks",
                    "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)",
                    "symbol VARCHAR(10) NOT NULL UNIQUE",
                    "curr_price DECIMAL(12, 2)",
                    "data_id INT NOT NULL REFERENCES stocks_data(id)",
                    "last_updated TIMESTAMP"
                    );
        }
    }

    @Override
    public void changeDB(String dbPath) throws DatabaseException
    {
        super.changeDB(dbPath);
        createTables();
    }
}