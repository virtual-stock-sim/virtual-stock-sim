package io.github.virtualstocksim.config;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Config
{
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private Properties config;

    private static class StaticContainer
    {
        private static final Config Instance = new Config();
    }

    private static Config getInstance() { return StaticContainer.Instance; }

    private Config()
    {
        try
        {
            // Read database configurations from properties file
            String configStr = IOUtils.toString(this.getClass().getResourceAsStream("/config"), StandardCharsets.UTF_8);
            config = new Properties();
            config.load(new StringReader(configStr));
        }
        catch (IOException e)
        {
            logger.error("Unable to read configuration file", e);
            System.exit(-1);
        }
    }

    /**
     * Get a configuration from the configuration file
     * @param key Configuration key
     * @return Configuration value
     */
    public static String getConfig(String key)
    {
        return getInstance().config.getProperty(key);
    }
}