package io.github.virtualstocksim.main;

import io.github.virtualstocksim.update.ClientUpdater;
import org.apache.commons.cli.*;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class Main
{
    public static final int DEFAULT_PORT = 8081;
    public static final String warDir = "./war";
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Options options = new Options();

    public static void main(String[] args) throws Exception
    {
        options.addOption("p", "port", true, "Port number to run server on");
        options.addOption("ssl", true, "Tells server to use SSL encryption with provided cert");
        options.addOption("sp", "securePort", true, "SSL/Secure port to run server on");
        options.addOption("kspass", "keyStorePass", true, "Key store password");

        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;
        try
        {
            cmd = parser.parse(options, args);
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage());
            printHelp();
            System.exit(-1);
        }

        // Attempt to get port
        int port = DEFAULT_PORT;
        if(cmd.hasOption("port"))
        {
            try
            {
                port = Integer.parseInt(cmd.getOptionValue("port"));
            }
            catch (NumberFormatException e)
            {
                logger.error("Port must be an integer");
                printHelp();
                System.exit(-1);
            }
        }

        Server server;
        if(cmd.hasOption("ssl"))
        {
            // Key store password and secure port must be specified
            if(!cmd.hasOption("keyStorePass") || !cmd.hasOption("securePort"))
            {
                logger.error("If using SSL, you must specify a key store password and secure port");
                printHelp();
                System.exit(-1);
            }
            // Attempt to get secure port
            int securePort = 0;
            try
            {
                securePort = Integer.parseInt(cmd.getOptionValue("securePort"));
            }
            catch (NumberFormatException e)
            {
                logger.error("Secure port must be an integer");
                printHelp();
                System.exit(-1);
            }
            server = createServer(port, securePort, cmd.getOptionValue("ssl"), cmd.getOptionValue("keyStorePass"), cmd.getOptionValue("keyManagerPass"));
        }
        else
        {
            server = createServer(port);
        }

        // Schedule the client updates
        ClientUpdater.scheduleStockUpdates();

        // Start server
        logger.info("Starting web server on port {}...\n", port);
        server.start();

        logger.info("Server started successfully");

        server.join();
    }

    private static Server createServer(int port)
    {
        return createServer(port, port ,null, null, null);
    }

    // sslCertPath == null means no ssl
    private static Server createServer(int port, int securePort, String sslCertPath, String keyStorePassword, String keyManagerPassword)
    {
        WebAppContext webAppContext = new WebAppContext();
        String warPath = new File(warDir).getAbsolutePath();
        webAppContext.setDescriptor(warPath + "/WEB-INF/web.xml");
        webAppContext.setContextPath("/");
        webAppContext.setWar(warPath);

        Server server;
        if(sslCertPath == null)
        {
            server = new Server(port);
        }
        else
        {
            // Make sure the cert path exists
            if(!Files.exists(Paths.get(sslCertPath)))
            {
                logger.error("Invalid SSL cert path");
                printHelp();
                System.exit(-1);
            }

            server = new Server();

            // Http connection
            HttpConfiguration httpConfig = new HttpConfiguration();
            httpConfig.setSecureScheme("https");
            httpConfig.setSecurePort(securePort);

            ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
            http.setPort(port);
            server.addConnector(http);

            // Https configuration
            HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
            httpsConfig.addCustomizer(new SecureRequestCustomizer());

            SslContextFactory.Server sslContext = new SslContextFactory.Server();
            sslContext.setKeyStorePath(sslCertPath);
            sslContext.setKeyStorePassword(keyStorePassword);

            ServerConnector https = new ServerConnector(server, new SslConnectionFactory(sslContext, HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(httpsConfig));
            https.setPort(securePort);
            server.addConnector(https);

            // Add security constraint override
            webAppContext.addOverrideDescriptor(warPath + "/WEB-INF/https.xml");
        }

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

    private static void printHelp()
    {
        HelpFormatter formatter = new HelpFormatter();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        formatter.printHelp(pw, formatter.getWidth(), "virtual-stock-sim", null, options, formatter.getLeftPadding(), formatter.getDescPadding(), null, true);
    }
}
