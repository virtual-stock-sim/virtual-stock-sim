package io.github.virtualstocksim.database;

import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.*;

public class DatabaseTests
{
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
