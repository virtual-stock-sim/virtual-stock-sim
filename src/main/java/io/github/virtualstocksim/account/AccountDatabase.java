package io.github.virtualstocksim.account;

import io.github.virtualstocksim.config.Config;
import io.github.virtualstocksim.database.DatabaseFactory;
import io.github.virtualstocksim.database.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class AccountDatabase
{
    private static final Logger logger = LoggerFactory.getLogger(AccountDatabase.class);
    private final DataSource dataSource;
    private static final String dbPath = Config.getConfig("accountdb.uri");

    private static class StaticContainer
    {
        private static final AccountDatabase Instance = new AccountDatabase();
    }

    private static AccountDatabase getInstance() { return AccountDatabase.StaticContainer.Instance; }

    private AccountDatabase()
    {
        dataSource = DatabaseFactory.getDatabase(dbPath);

        try
        {
            createTables();
        }
        catch (SQLException e)
        {
            logger.error("Failed to check for and/or create missing account database tables\n", e);
            System.exit(-1);
        }
    }

    public static Connection getConnection() throws SQLException
    {
        return getInstance().dataSource.getConnection();
    }

    // Create the database tables if they don't exist
    private void createTables() throws SQLException
    {
        try(Connection conn = dataSource.getConnection())
        {
            if(!SQL.tableExists(conn, "account"))
            {
                SQL.executeUpdate(conn, "create table account (id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                                          "uuid VARCHAR(36) NOT NULL UNIQUE," +
                                          "type VARCHAR(16) NOT NULL," +
                                          "username VARCHAR(255) NOT NULL UNIQUE," +
                                          "email VARCHAR(255) NOT NULL UNIQUE," +
                                          "password_hash VARCHAR(256) FOR BIT DATA NOT NULL," +
                                          "password_salt VARCHAR(16) FOR BIT DATA NOT NULL," +
                                          "followed_stocks LONG VARCHAR," +
                                          "invested_stocks LONG VARCHAR," +
                                          "transaction_history CLOB," +
                                          "wallet_balance DECIMAL(12, 2)," +
                                          "leaderboard_rank INT," +
                                          "bio VARCHAR(500)," +
                                          "profile_picture LONG VARCHAR," +
                                          "creation_date TIMESTAMP)"
                                 );
            }

            if(!SQL.tableExists(conn, "reset_token"))
            {
                SQL.executeUpdate(conn, "create table reset_token (id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                                          "account_id INT NOT NULL REFERENCES account(id)," +
                                          "token VARCHAR(255) NOT NULL UNIQUE" +
                                          "expiration TIMESTAMP NOT NULL)"
                                 );
            }
        }
    }
}
