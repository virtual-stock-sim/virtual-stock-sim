package io.github.virtualstocksim.database;

public abstract class DatabaseItem
{
    public final int id;
    protected DatabaseItem(int id) { this.id = id; }

    public abstract void commit();
}
