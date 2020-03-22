package io.github.virtualstocksim.database;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseFactory
{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFactory.class);
    private static Map<String, BasicDataSource> dataSources;
    private static Properties config;

    private static class StaticContainer
    {
        private static final DatabaseFactory Instance = new DatabaseFactory();
    }

    private DatabaseFactory()
    {
        dataSources = new ConcurrentHashMap<>();

        try
        {
            // Read database configurations from properties file
            String configStr = IOUtils.toString(this.getClass().getResourceAsStream("/databases.config"), StandardCharsets.UTF_8);
            config = new Properties();
            config.load(new StringReader(configStr));
        }
        catch (IOException e)
        {
            logger.error("Unable to read database.config", e);
            System.exit(-1);
        }
    }

    /**
     *
     * @param databaseURI URI for database
     * @return Connection driver
     * @see DataSource
     */
    public static DataSource getDatabase(String databaseURI)
    {
        // Return existing data source for database if exists
        if(!dataSources.containsKey(databaseURI))
        {
            return dataSources.get(databaseURI);
        }
        // Else create and return a new data source
        else
        {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriver(new EmbeddedDriver());
            ds.setUrl(databaseURI);

            return dataSources.put(databaseURI, ds);
        }
    }

    public static String getConfig(String key)
    {
        return config.getProperty(key);
    }

}