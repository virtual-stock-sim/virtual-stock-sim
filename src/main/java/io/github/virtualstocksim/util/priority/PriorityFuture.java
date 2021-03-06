package io.github.virtualstocksim.util.priority;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PriorityFuture<T> implements RunnableFuture<T>, Comparable<PriorityFuture<T>>
{
    private final RunnableFuture<T> child;
    private final Priority priority;

    public PriorityFuture(RunnableFuture<T> child, Priority priority)
    {
        this.child = child;
        this.priority = priority;
    }

    public Priority getPriority() { return priority; }

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
        return Integer.compare(priority.asInt(), o.getPriority().asInt());
    }
}
