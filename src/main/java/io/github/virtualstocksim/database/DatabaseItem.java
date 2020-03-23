package io.github.virtualstocksim.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseItem
{
    protected final int id;
    protected DatabaseItem(int id) { this.id = id; }

    public int getId() { return id; }

    public abstract void update() throws SQLException;
    public abstract void update(Connection conn) throws SQLException;

    public abstract void delete() throws SQLException;
    public abstract void delete(Connection conn) throws SQLException;
}