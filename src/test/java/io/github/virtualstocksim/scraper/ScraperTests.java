package io.github.virtualstocksim.scraper;

import io.github.virtualstocksim.stock.stockrequest.StockResponseCode;
import io.github.virtualstocksim.util.Result;
import io.github.virtualstocksim.util.priority.Priority;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

import static org.junit.Assert.fail;

public class ScraperTests
{
    private static final Logger logger = LoggerFactory.getLogger(ScraperTests.class);

    @Test
    public void testExists() throws InterruptedException, ExecutionException
    {
        runTestFunction(p -> Scraper.checkStockExists(p.getLeft(), p.getRight()));
    }

    @Test
    public void testGetDescription() throws InterruptedException, ExecutionException
    {
        runTestFunction(p -> Scraper.getDescription(p.getLeft(), p.getRight()));
    }

    @Test
    public void testGetDescAndHist() throws InterruptedException, ExecutionException
    {
        runTestFunction(p -> Scraper.getDescriptionAndHistory(p.getLeft(), TimeInterval.ONEMONTH, p.getRight()));
    }

    private static final List<Pair<String, Priority>> symbols = new LinkedList<>();
    static
    {
        symbols.add(new ImmutablePair<>("GOOGL", Priority.MEDIUM));
        symbols.add(new ImmutablePair<>("BDX", Priority.MEDIUM));
        symbols.add(new ImmutablePair<>("RCL", Priority.LOW));
        symbols.add(new ImmutablePair<>("DISCK", Priority.URGENT));
        symbols.add(new ImmutablePair<>("aaaaasdlksdf", Priority.LOW));
        symbols.add(new ImmutablePair<>("^NASDAQ", Priority.HIGH));
        symbols.add(new ImmutablePair<>("AAPL", Priority.URGENT));
    }

    private static <T> void runTestFunction(Function<Pair<String, Priority>, Result<T, StockResponseCode>> func) throws InterruptedException, ExecutionException
    {
        final ExecutorService executor = Executors.newCachedThreadPool();
        List<Callable<Boolean>> tasks = new LinkedList<>();

        for(Pair<String, Priority> p : symbols)
        {
            tasks.add(() ->
                      {
                          Result<T, StockResponseCode> r = func.apply(p);
                          logger.info("Stock symbol: " + p.getLeft());
                          if(r.isError())
                          {
                              if(p.getLeft().equals("aaaaasdlksdf") || p.getLeft().contains("^NASDAQ"))
                              {
                                  logger.info("Error correctly returned" + r.getError().toString());
                                  return true;
                              }
                              else
                              {
                                  logger.error("Bad error: " + r.getError().toString());
                                  return false;
                              }
                          }
                          else
                          {
                              logger.info("Result: " + r.getValue());
                              return true;
                          }
                      });
        }

        List<Future<Boolean>> futures = executor.invokeAll(tasks);
        for(Future<Boolean> future : futures)
        {
            if(!future.get())
            {
                fail();
            }
        }
    }
}
