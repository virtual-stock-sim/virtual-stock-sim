package io.github.virtualstocksim.database;

import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.*;

public class DatabaseTests
{
    @ClassRule
    public static DatabaseConnections databases = new DatabaseConnections();

    @Test
    public void testTableExists()
    {
        try
        {
            String tableName = "test_table";
            assertFalse(databases.getGenericDB().tableExists(tableName));
            databases.getGenericDB().createTable(tableName, "id INT NOT NULL");
            assertTrue(databases.getGenericDB().tableExists(tableName));
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
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
