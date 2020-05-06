package io.github.virtualstocksim.util.priority;

public enum Priority
{
    // This needs to be executed immediately over any other task
    URGENT,
    // This needs to be executed over most other tasks
    HIGH,
    // This can wait for more important tasks to execute first
    MEDIUM,
    // This can wait as long as necessary
    LOW;

    public int getPriority() { return ordinal(); }
}