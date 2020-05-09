package io.github.virtualstocksim.scraper;

import io.github.virtualstocksim.config.Config;
import io.github.virtualstocksim.util.priority.PriorityCallable;
import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

class ScraperTaskSubmitter<T> extends PriorityCallable<T>
{
    private static final Logger logger = LoggerFactory.getLogger(ScraperTaskSubmitter.class);
    private static final AtomicLong LAST_TASK = new AtomicLong(0);
    private static final long DELAY;
    static
    {
        long _delay = 0;
        try
        {
            _delay = TimeUnit.SECONDS.toMillis(Long.parseLong(Config.getConfig("scraper.delay")));
        }
        catch (NumberFormatException e)
        {
            logger.error("Unable to parse configuration `scraper.delay` to long\n", e);
            System.exit(-1);
        }

        DELAY = _delay;
    }
    
    private final PriorityCallable<T> child;
    public ScraperTaskSubmitter(PriorityCallable<T> child)
    {
        super(child.getPriority());
        this.child = child;
    }

    @Override
    public T call() throws Exception
    {
        // Has DELAY time passed between last scrape?
        long now = Instant.now().toEpochMilli();
        long lastScrape = LAST_TASK.get();
        if(now - DELAY <= lastScrape)
        {
            long timeDiff = now - lastScrape;
            long sleepTime = DELAY - timeDiff;
            logger.info(
                    "Last scraper task was " + ((double) timeDiff / 1000.0) + " seconds ago. " +
                            "Sleeping for " + ((double) sleepTime / 1000.0) + " seconds before next task"
                       );
            // Sleep for the remaining time left in the desired delay
            TimeUnit.MILLISECONDS.sleep(sleepTime);
        }
        int attempts = 0;
        while(true)
        {
            try
            {
                // Execute the task
                T result = child.call();
                // Update the last task execution time
                LAST_TASK.set(Instant.now().toEpochMilli());
                return result;
            }
            catch (ExecutionException e)
            {
                if (e.getCause() instanceof HttpStatusException)
                {
                    // Only throw the exception is max attempts are exceeded, otherwise
                    // try again
                    if(++attempts >= 2)
                    {
                        throw e;
                    }
                }
                else
                {
                    throw e;
                }
            }
        }
    }
}