package io.github.virtualstocksim.util.priority;

import java.util.concurrent.Callable;

public abstract class PriorityCallable<T> implements Callable<T>, Comparable<PriorityCallable<T>>
{
    private final Priority priority;
    protected PriorityCallable(Priority priority)
    {
        this.priority = priority;
    }
    public Priority getPriority() { return priority; }

    @Override
    public int compareTo(PriorityCallable<T> o)
    {
        return Integer.compare(priority.asInt(), o.getPriority().asInt());
    }
}
