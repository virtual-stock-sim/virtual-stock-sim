package io.github.virtualstocksim.stock;


import io.github.virtualstocksim.config.Config;
import io.github.virtualstocksim.database.DatabaseFactory;
import io.github.virtualstocksim.database.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class StockDatabase
{
    private static final Logger logger = LoggerFactory.getLogger(StockData.class);
    private final DataSource dataSource;
    private final String dbPath = Config.getConfig("stockdb.uri");
    private final int maxSymbolLen;


    private static class StaticContainer
    {
        private static final StockDatabase Instance = new StockDatabase();
    }

    private static StockDatabase getInstance() { return StaticContainer.Instance; }

    private StockDatabase()
    {
        dataSource = DatabaseFactory.getDatabase(dbPath);

        int _maxSymbolLen = 0;
        try
        {
            _maxSymbolLen = Integer.parseInt(Config.getConfig("stockdb.maxSymbolLen"));
            createTables();
        }
        catch (NumberFormatException e)
        {
            logger.error("stockdb.maxSymbolLen must be an integer");
            System.exit(-1);
        }
        catch (SQLException e)
        {
            logger.error("Failed to check for and/or create missing stock database tables\n", e);
            System.exit(-1);
        }

        maxSymbolLen = _maxSymbolLen;
    }

    public static Connection getConnection() throws SQLException
    {
        return getInstance().dataSource.getConnection();
    }

    public static int getMaxSymbolLen()
    {
        return getInstance().maxSymbolLen;
    }

    // Create the database tables if they don't exist
    private void createTables() throws SQLException
    {
        try(Connection conn = dataSource.getConnection())
        {
            if(!SQL.tableExists(conn, "stock_data"))
            {
                SQL.executeUpdate(conn, "create table STOCK_DATA(id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                                          "data CLOB NOT NULL," +
                                          "last_updated TIMESTAMP)"
                                 );
            }

            if(!SQL.tableExists(conn, "stock"))
            {
                SQL.executeUpdate(conn, "create table STOCK(id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                                          "symbol VARCHAR(10) NOT NULL UNIQUE," +
                                          "curr_price DECIMAL(12, 2)," +
                                          "prev_close DECIMAL(12, 2)," +
                                          "curr_volume INT," +
                                          "prev_volume INT," +
                                          "data_id INT NOT NULL REFERENCES stock_data(id)," +
                                          "last_updated TIMESTAMP)"
                                 );
            }
        }
    }

}