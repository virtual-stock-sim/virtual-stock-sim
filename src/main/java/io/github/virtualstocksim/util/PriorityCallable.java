package io.github.virtualstocksim.util;

import java.util.concurrent.Callable;

public abstract class PriorityCallable<T> implements Callable<T>, Comparable<PriorityCallable<T>>
{
    private final int priority;
    protected PriorityCallable(int priority)
    {
        this.priority = priority;
    }
    public int getPriority() { return priority; }

    @Override
    public int compareTo(PriorityCallable<T> o)
    {
        return Integer.compare(priority, o.getPriority());
    }
}
