package io.github.virtualstocksim.database;

public abstract class DatabaseItem
{
    protected final int id;
    protected DatabaseItem(int id) { this.id = id; }

    public int getId() { return id; }

    public abstract void commit() throws DatabaseException;
}