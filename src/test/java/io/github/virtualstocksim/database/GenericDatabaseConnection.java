package io.github.virtualstocksim.database;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.fail;

public class GenericDatabaseConnection extends ExternalResource
{
    public static final Logger logger = LoggerFactory.getLogger(GenericDatabaseConnection.class);

    public final String genericDBPath = String.format("testdbs/test_generic_database%s.db", UUID.randomUUID().toString());

    protected Database genericDB = null;
    public Database getGenericDB()
    {
        return genericDB;
    }
    @Override
    protected void before() throws Throwable
    {
        try
        {
            genericDB = new Database(genericDBPath);
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
            genericDB.closeConn();
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


        File db = new File(genericDBPath);
        if(db.exists())
        {
            db.delete();
        }
    }
}
