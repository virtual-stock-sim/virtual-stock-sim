package io.github.virtualstocksim.database;

import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class DatabaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseTests.class);
    @ClassRule
    public static GenericDatabaseConnection conn = new GenericDatabaseConnection();

    @Test
    public void testTableExists()
    {
        try
        {
            String tableName = "test_table";
            assertFalse(conn.getGenericDB().tableExists(tableName));
            conn.getGenericDB().createTable(tableName, "id INT NOT NULL");
            assertTrue(conn.getGenericDB().tableExists(tableName));
        } catch (DatabaseException e)
        {
            logger.error("", e);
            fail();
        }
    }

/*
    @Test
    public void testCreateTable()
    {
    }
*/

/*    @Test
    public void testExecuteStmt()
    {
    }*/
}
