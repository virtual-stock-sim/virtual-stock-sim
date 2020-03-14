package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.Database;
import io.github.virtualstocksim.util.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

// Controller for stock data cache
public class StockCache extends Database
{
    private static final Logger logger = LoggerFactory.getLogger(StockCache.class);

    private static String dbPath = "vss_stockcache.db";

    private static Lazy<StockCache> singleton = Lazy.lazily(StockCache::new);
    public static StockCache Instance()
    {
        return singleton.get();
    }

    private StockCache()
    {
        super(dbPath);
        try
        {
            createTables();
        } catch (SQLException e)
        {
            logger.error("Unable to create tables for stock cache");
            System.exit(-1);
        }
    }

    // Create tables if they don't exist
    private void createTables() throws SQLException
    {
        if(!tableExists("stocks_data"))
        {
            createTable("stocks_data",
                    "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)",
                    "data LONG VARCHAR NOT NULL"
                    );
        }

        if(!tableExists("stocks"))
        {
            createTable("stocks",
                    "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)",
                    "symbol VARCHAR(10) NOT NULL UNIQUE",
                    "curr_price DECIMAL(12, 2)",
                    "data_id INT NOT NULL REFERENCES stocks_data(id)"
                    );
        }
    }

    public static void changeDatabase(String dbPath)
    {
        // Attempt to close current connection if there is one
        try
        {
            if(singleton.hasEvaluated()) Instance().closeConnection();
        } catch (SQLException e)
        {
            logger.error("Error closing database connection for stock cache\nError: " + e.getMessage());
            return;
        }

        // Create new singleton with new database
        StockCache.dbPath = dbPath;
        singleton = Lazy.lazily(StockCache::new);
    }

}