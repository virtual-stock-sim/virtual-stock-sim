package io.github.virtualstocksim.main;

import io.github.virtualstocksim.update.ClientUpdater;
import io.github.virtualstocksim.update.StockUpdater;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
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
        Server server = createServer();

        // Schedule the client updates
        ClientUpdater.scheduleStockUpdates();

        // Start server
        logger.info("Starting web server on port {}...\n", PORT);
        server.start();

        logger.info("Server started successfully");

        server.join();
    }

    private static Server createServer()
    {
        Server server = new Server(PORT);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setWar(new File(warDir).getAbsolutePath());

        Configuration.ClassList classList = Configuration.ClassList.setServerDefault(server);
        classList.addBefore(
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration");

        // The following 3 calls are from the Embedded Jetty Example: https://www.eclipse.org/jetty/documentation/9.4.x/embedded-examples.html#embedded-webapp-jsp
        webAppContext.setAttribute(
                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$" );
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.welcomeServlets", "true");

        server.setHandler(webAppContext);

        return server;
    }
}
