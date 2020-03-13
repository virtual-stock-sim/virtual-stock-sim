package io.github.virtualstocksim.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;

// Generic database controller
public class Database
{
    protected Connection db;
    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    public final String dbPath;

    public Database(String dbPath)
    {
        this.dbPath = dbPath;
        try
        {
            db = DriverManager.getConnection(String.format("jdbc:derby:%s;create=true", dbPath));
        }
        catch (SQLException e)
        {
            logSqlError("Unable to open connection to and/or create database", e);
            System.exit(-1);
        }
    }

    public void closeConnection() throws SQLException
    {
        db.close();
    }

    public boolean tableExists(String table) throws SQLException
    {
        logger.info(String.format("Checking if table %s exists in %s", table, dbPath));
        DatabaseMetaData metaData = db.getMetaData();
        ResultSet rs = metaData.getTables(null, "APP", table.toUpperCase(), null);

        return rs.next() && rs.getString(3).equals(table.toUpperCase());
    }

    // Create table `name` with `columns`
        // Columns should the whole column declaration
        // i.e. "id INT NOT NULL"
    public void createTable(String name, String... columns) throws SQLException
    {
        logger.info("Creating table " + name);
        StringBuilder sql = new StringBuilder(String.format("CREATE TABLE %s(", name));
        for(String column : columns)
        {
            sql.append(column).append(",");
        }
        sql.deleteCharAt(sql.length()-1);
        sql.append(")");
        executeStmt(sql.toString());
    }

    protected static Object[] emptyObjArr = {};

    // Execute basic sql
    public void executeStmt(String sql) throws SQLException
    {
        executeStmt(sql, emptyObjArr);
    }
    // Execute prepared sql statement with parameters
    public void executeStmt(String sql, Object... values) throws SQLException
    {
        logSqlExecute(sql, values);
        PreparedStatement stmt = db.prepareStatement(sql);
        for(int i = 0; i < values.length; ++i)
        {
            stmt.setObject(i, values[i]);
        }
        stmt.execute();
    }

    // Execute basic sql query
    public ResultSet executeQuery(String sql) throws SQLException
    {
        return executeQuery(sql, emptyObjArr);
    }
    // Execute prepared sql query with parameters
    public ResultSet executeQuery(String sql, Object... values) throws SQLException
    {
        logSqlExecute(sql, values);
        PreparedStatement stmt = db.prepareStatement(sql);
        for(int i = 0; i < values.length; ++i)
        {
            stmt.setObject(i+1, values[i]);
        }
        return stmt.executeQuery();
    }

    // Execute basic sql update
    public int executeUpdate(String sql) throws SQLException
    {
        return executeUpdate(sql, emptyObjArr);
    }
    // Execute prepared sql update with parameters
        // Returns # of rows effected
    public int executeUpdate(String sql, Object... values) throws SQLException
    {
        logSqlExecute(sql, values);
        PreparedStatement stmt = db.prepareStatement(sql);
        for(int i = 0; i < values.length; ++i)
        {
            stmt.setObject(i+1, values[i]);
        }
        return stmt.executeUpdate();
    }

    // Execute basic sql insertion
    public int executeInsert(String sql) throws SQLException
    {
        return executeInsert(sql, emptyObjArr);
    }
    // Execute prepared sql insertion with parameters
        // Returns generated key from insertion
    public int executeInsert(String sql, Object... values) throws SQLException
    {
        logSqlExecute(sql, values);
        PreparedStatement stmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for(int i = 0; i < values.length; ++i)
        {
            stmt.setObject(i+1, values[i]);
        }
        stmt.executeUpdate();

        // Return key from insertion
        ResultSet rs = stmt.getGeneratedKeys();
        if(rs.next())
        {
            return rs.getInt(1);
        }
        else
        {
            return 0;
        }
    }

    public void logSqlError(String msg, SQLException e)
    {
        logger.error(String.format("==SQL Error==\n%s\nDatabase: %s\nException: %s\nState: %s\nError Code: %d", msg, dbPath, e.getMessage(), e.getSQLState(), e.getErrorCode()));
    }

    public void logSqlExecute(String sql, Object[] values)
    {
        logger.info(String.format("SQL: %s | Params: %s", sql, Arrays.toString(values)));
    }
}
