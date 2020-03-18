package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.Database;
import io.github.virtualstocksim.stock.StockCache;
import io.github.virtualstocksim.util.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static io.github.virtualstocksim.stock.StockCache.Instance;

public class AccountDatabase extends Database
{
    private static final Logger logger = LoggerFactory.getLogger(AccountDatabase.class);

    private static String dbPath = "vss_accounts.db";

    private static Lazy<AccountDatabase> singleton = Lazy.lazily(AccountDatabase::new);
    public static AccountDatabase Instance()
    {
        return singleton.get();
    }


    private AccountDatabase()
    {
        super(dbPath);
        try
        {
            createTables();
        } catch (SQLException e)
        {
            logger.error("Unable to create tables for Account Database");
            System.exit(-1);
        }
    }

    // Create tables if they don't exist
    private void createTables() throws SQLException
    {
        if(!tableExists("accounts"))
        {
            createTable("accounts",
                    "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)",
                    "uuid VARCHAR(256) NOT NULL UNIQUE",
                    "type VARCHAR(16) NOT NULL",
                    "username LONG VARCHAR NOT NULL",
                    "password_hash VARCHAR(256) FOR BIT DATA NOT NULL",
                    "password_salt VARCHAR(16) FOR BIT DATA NOT NULL",
                    "followed_stocks LONG VARCHAR",
                    "transaction_history LONG VARCHAR",
                    "leaderboard_rank INT",
                    "bio LONG VARCHAR",
                    "profile_picture LONG VARCHAR"

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
            logger.error("Error closing database connection for account database\nError: " + e.getMessage());
            return;
        }

        // Create new singleton with new database
        AccountDatabase.dbPath = dbPath;
        singleton = Lazy.lazily(AccountDatabase::new);
    }



}
