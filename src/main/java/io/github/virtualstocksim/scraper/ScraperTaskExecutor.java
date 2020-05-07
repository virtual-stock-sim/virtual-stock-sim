package io.github.virtualstocksim.scraper;

import io.github.virtualstocksim.util.priority.PriorityCallable;
import io.github.virtualstocksim.util.priority.PriorityFuture;

import java.util.concurrent.*;

public class ScraperTaskExecutor extends ThreadPoolExecutor
{
    public ScraperTaskExecutor()
    {
        super(0, 1, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable)
    {
        RunnableFuture<T> rf = super.newTaskFor(callable);
        return new PriorityFuture<>(rf, ((PriorityCallable<T>) callable).getPriority());
    }
}
