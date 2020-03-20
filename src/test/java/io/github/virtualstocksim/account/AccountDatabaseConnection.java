package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.DatabaseException;
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

public class AccountDatabaseConnection extends ExternalResource
{
    private static final Logger logger = LoggerFactory.getLogger(AccountDatabaseConnection.class);
    public final String accountDBPath = String.format("testdbs/test_account_database%s.db", UUID.randomUUID().toString());

    private AccountDatabase accDB = AccountDatabase.Instance();
    public AccountDatabase getAccDB()
    {
        return accDB;
    }

    @Override
    protected void before() throws Throwable
    {
        accDB.changeDB(accountDBPath);


    }

    @Override
    protected void after()
    {
        try
        {
            accDB.closeConn();
        }
        catch (DatabaseException e)
        {
            logger.error("",e);
            fail();
        }

        try
        {
            DriverManager.getConnection(String.format("jdbc:derby:%s;shutdown=true", accountDBPath));
        }
        catch (SQLException e) {}

        File file = new File(accountDBPath);
        if(file.exists())
        {
            try
            {
                FileUtils.deleteDirectory(file);
            } catch (IOException e)
            {
                System.err.println("Couldn't delete temporary test folder");
                System.err.println(e.getMessage());
            }
        }
    }
}
