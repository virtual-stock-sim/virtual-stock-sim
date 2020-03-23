package io.github.virtualstocksim.database;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseFactory
{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFactory.class);
    private Map<String, BasicDataSource> dataSources;
    private Properties config;

    private static class StaticContainer
    {
        private static final DatabaseFactory Instance = new DatabaseFactory();
    }

    private static DatabaseFactory getInstance() { return StaticContainer.Instance; }

    private DatabaseFactory()
    {
        dataSources = new ConcurrentHashMap<>();
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
        if(getInstance().dataSources.containsKey(databaseURI))
        {
            return getInstance().dataSources.get(databaseURI);
        }
        // Else create and return a new data source
        else
        {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriver(new EmbeddedDriver());
            ds.setUrl(databaseURI);
            getInstance().dataSources.put(databaseURI, ds);

            return getInstance().dataSources.get(databaseURI);
        }
    }
}