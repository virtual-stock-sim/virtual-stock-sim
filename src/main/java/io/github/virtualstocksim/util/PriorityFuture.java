package io.github.virtualstocksim.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PriorityFuture<T> implements RunnableFuture<T>, Comparable<PriorityFuture<T>>
{
    private final RunnableFuture<T> child;
    private final int priority;

    public PriorityFuture(RunnableFuture<T> child, int priority)
    {
        this.child = child;
        this.priority = priority;
    }

    public int getPriority() { return priority; }

    @Override
    public void run()
    {
        child.run();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return child.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled()
    {
        return child.isCancelled();
    }

    @Override
    public boolean isDone()
    {
        return child.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException
    {
        return child.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        return child.get(timeout, unit);
    }

    @Override
    public int compareTo(PriorityFuture<T> o)
    {
        return Integer.compare(priority, o.getPriority());
    }
}
