package io.github.virtualstocksim.database;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;

import java.io.File;
import java.io.IOException;

@Ignore
public class DatabaseTestsBase
{
    public static String testFolderPath = "testTemp";
    public static String dbPath = testFolderPath + "/test_database.db";

    @BeforeClass
    public static void setup()
    {
        // Delete temporary test files leftover from last run
        deleteTempFolder();
    }

    private static void deleteTempFolder()
    {
        File file = new File(testFolderPath);
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
