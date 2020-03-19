package io.github.virtualstocksim.database;

import java.sql.SQLException;

public class DatabaseException extends RuntimeException
{
    private String dbPath;
    private SQLException sqlEx;

    public DatabaseException(String msg, String dbPath, SQLException e)
    {
        super(msg, e, false, true);
        this.sqlEx = null;
    }

    public String getDbPath()
    {
        return dbPath;
    }

    public SQLException getSqlException()
    {
        return sqlEx;
    }
}