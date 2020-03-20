package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.Database;
import io.github.virtualstocksim.database.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountDatabase extends Database
{
    private static final Logger logger = LoggerFactory.getLogger(AccountDatabase.class);
    private final static String initDBPath = "vss_accounts.db";

    private static AccountDatabase singleton = init();
    public static AccountDatabase Instance()
    {
        return singleton;
    }

    private static AccountDatabase init()
    {
        try
        {
            return new AccountDatabase(initDBPath);
        }
        catch (DatabaseException e)
        {
            logger.error(String.format("Unable to open connection to account database\n%s", e));
            System.exit(-1);
        }
        return null;
    }

    private AccountDatabase(String dbPath) throws DatabaseException
    {
        super(dbPath, logger);
        try
        {
            createTables();
        } catch (DatabaseException e)
        {
            logger.error(String.format("Unable to create tables for account database\n%s", e));
            System.exit(-1);
        }
    }

    // Create tables if they don't exist
    private void createTables() throws DatabaseException
    {
        if(!tableExists("accounts"))
        {
            createTable("accounts",
                    "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)",
                    "uuid VARCHAR(36) NOT NULL UNIQUE",
                    "type VARCHAR(16) NOT NULL",
                    "username VARCHAR(255) NOT NULL",
                    "email VARCHAR(255) NOT NULL UNIQUE",
                    "password_hash VARCHAR(256) FOR BIT DATA NOT NULL",
                    "password_salt VARCHAR(16) FOR BIT DATA NOT NULL",
                    "followed_stocks LONG VARCHAR",
                    "transaction_history LONG VARCHAR",
                    "leaderboard_rank INT",
                    "bio LONG VARCHAR",
                    "profile_picture LONG VARCHAR",
                    "creation_date TIMESTAMP"

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
