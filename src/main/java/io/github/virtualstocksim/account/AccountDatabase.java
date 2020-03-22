package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class AccountDatabase
{
    private static final Logger logger = LoggerFactory.getLogger(AccountDatabase.class);
    private final DataSource dataSource;
    private static final String dbPath = DatabaseFactory.getConfig("accountdb.uri");

    private static class StaticContainer
    {
        private static final AccountDatabase Instance = new AccountDatabase();
    }

    private static AccountDatabase getInstance() { return AccountDatabase.StaticContainer.Instance; }

    private AccountDatabase()
    {
        dataSource = DatabaseFactory.getDatabase(dbPath);
    }

    public static Connection getConnection() throws SQLException
    {
        return getInstance().dataSource.getConnection();
    }
}
