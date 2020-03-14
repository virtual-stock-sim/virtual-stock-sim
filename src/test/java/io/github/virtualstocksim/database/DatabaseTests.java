package io.github.virtualstocksim.database;

import org.junit.Test;

import static org.junit.Assert.*;

public class DatabaseTests extends DatabaseTestsBase
{

    @Test
    public void testTableExists()
    {
        Database db = new Database(dbPath);
        try
        {
            String tableName = "test_table";
            assertFalse(db.tableExists(tableName));
            db.createTable(tableName, "id INT NOT NULL");
            assertTrue(db.tableExists(tableName));
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void testCreateTable()
    {
    }

    @Test
    public void testExecuteStmt()
    {
    }
}
