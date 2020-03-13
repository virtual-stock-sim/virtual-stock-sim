package io.github.virtualstocksim.database;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class DatabaseTests
{
    private static String dbPath = "database_test.db";

    @Before
    public void setup()
    {
        // Delete old test db if exists
        File file = new File(dbPath);
        if(file.exists())
        {
            try
            {
                FileUtils.deleteDirectory(file);
            } catch (IOException e)
            {
                System.err.println("Couldn't delete test db");
                System.err.println(e.getMessage());
            }
        }
    }

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
