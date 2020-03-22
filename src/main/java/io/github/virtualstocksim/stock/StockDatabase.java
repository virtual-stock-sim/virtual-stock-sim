package io.github.virtualstocksim.stock;


import io.github.virtualstocksim.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class StockDatabase
{
    private static final Logger logger = LoggerFactory.getLogger(StockData.class);
    private final DataSource dataSource;
    private static final String dbPath = DatabaseFactory.getConfig("stockdb.uri");

    private static class StaticContainer
    {
        private static final StockDatabase Instance = new StockDatabase();
    }

    private static StockDatabase getInstance() { return StaticContainer.Instance; }

    private StockDatabase()
    {
        dataSource = DatabaseFactory.getDatabase(dbPath);
    }

    public static Connection getConnection() throws SQLException
    {
        return getInstance().dataSource.getConnection();
    }

}
