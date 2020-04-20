package io.github.virtualstocksim.update;

import io.github.virtualstocksim.config.Config;
import io.github.virtualstocksim.scraper.TimeInterval;
import io.github.virtualstocksim.servlet.DataStreamServlet;
import io.github.virtualstocksim.stock.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Pushes new data to connected clients
 */
public class ClientUpdater
{
    private static final Logger logger = LoggerFactory.getLogger(ClientUpdater.class);

    private Duration stockUpdateInterval;
    public static Duration getStockUpdateInterval() { return getInstance().stockUpdateInterval; }
    private Duration stockDataUpdateInterval;
    public static Duration getStockDataUpdateInterval() { return getInstance().stockDataUpdateInterval; }

    private final ScheduledExecutorService scheduler;

    private static class StaticContainer
    {
        private static final ClientUpdater Instance = new ClientUpdater();
    }

    private static ClientUpdater getInstance() { return StaticContainer.Instance; }

    private ClientUpdater()
    {
        scheduler = Executors.newScheduledThreadPool(2);

        try
        {
            stockUpdateInterval = parseInterval("update.stock.interval");
            stockDataUpdateInterval = parseInterval("update.stockdata.interval");
        }
        catch (DateTimeParseException | IllegalArgumentException e)
        {
            logger.error("Exception parsing stock/stock data timer configs\n", e);
            System.exit(-1);
        }
    }

    private static Duration parseInterval(String intervalConfigName) throws IllegalArgumentException
    {
        int[] intervalConfig = Arrays.stream(Config.getConfig(intervalConfigName).split(":")).mapToInt(Integer::parseInt).toArray();

        if(intervalConfig.length != 3)
        {
            throw new IllegalArgumentException("Invalid stock update interval configuration");
        }

        return Duration.ofHours(intervalConfig[0])
                        .plus(Duration.ofMinutes(intervalConfig[1]))
                        .plus(Duration.ofSeconds(intervalConfig[2]));
    }

    private void scheduleUpdateTask(Duration interval, String startTime, Runnable task)
    {
        long startDelay;
        if(startTime.equals("immediately"))
        {
            startDelay = 0;
        }
        else if(startTime.equals("wait-interval"))
        {
            startDelay = interval.toNanos();
        }
        else
        {
            LocalTime time = LocalTime.parse(startTime);
            LocalDateTime ldt = LocalDateTime.of(LocalDate.now(), time);
            if(ChronoUnit.NANOS.between(LocalDateTime.now(), ldt) < 0)
            {
                ldt = ldt.plusDays(1);
            }
            startDelay = ChronoUnit.NANOS.between(LocalDateTime.now(), ldt);
        }

        scheduler.scheduleAtFixedRate(task, startDelay, interval.toNanos(), TimeUnit.NANOSECONDS);
    }

    public static void scheduleStockUpdates()
    {
        getInstance().scheduleUpdateTask(getStockUpdateInterval(), Config.getConfig("update.stock.start"), () ->
        {
            try
            {
                StockUpdater.updateStocks(Stock.FindAll());
                String message = "{\"update\": \"stock\"}";
                for(AsyncContext ac : DataStreamServlet.getConnectedClients().values())
                {
                    DataStreamServlet.sendSimpleMessage(ac, message);
                }
            }
            catch (UpdateException e)
            {
                logger.info("Exception while updating stocks\n", e);
            }
        });

        getInstance().scheduleUpdateTask(getStockDataUpdateInterval(), Config.getConfig("update.stockdata.start"), () ->
        {
            try
            {
                StockUpdater.updateStockDatas(Stock.FindAll(), TimeInterval.ONEMONTH, 10, 1, 3);
                String message = "{\"update\": \"stockData\"}";
                for(AsyncContext ac : DataStreamServlet.getConnectedClients().values())
                {
                    DataStreamServlet.sendSimpleMessage(ac, message);
                }
            }
            catch (UpdateException e)
            {
                logger.error("Exception while updating stocks\n", e);
            }
        });
    }

}