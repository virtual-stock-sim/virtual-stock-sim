package io.github.virtualstocksim.main;

import io.github.virtualstocksim.update.ClientUpdater;
import io.github.virtualstocksim.update.StockUpdater;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Main
{
    public static final int PORT = 8081;
    public static final String warDir = "./war";
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception
    {
        // Configure server
        File warFile = new File(warDir);
        Server server = new Launcher().launch(false, PORT, warFile.getAbsolutePath(), "/");

        // Schedule the client updates
        ClientUpdater.scheduleStockUpdates();

        // Start server
        logger.info("Starting web server on port {}...\n", PORT);
        server.start();

        logger.info("Server started successfully");

        server.join();
    }
}
