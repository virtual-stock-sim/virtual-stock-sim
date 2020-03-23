package io.github.virtualstocksim.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.Arrays;

/**
 * Static methods for executing SQL
 */
public class SqlCmd
{
    private static final Logger logger = LoggerFactory.getLogger(SqlCmd.class);
    private static final RowSetFactory rowSetFac = getRowSetFactory();

    private static RowSetFactory getRowSetFactory()
    {
        try
        {
            return RowSetProvider.newFactory();
        }
        catch (SQLException e)
        {
            logger.error("Couldn't create a row set factory for CachedRowSet creation. System exiting.\n", e);
            System.exit(-1);
        }

        return null;
    }

    // Empty array for overloaded methods
    private static final Object[] emptyObjArr = {};

    /**
     * Executes SQL command
     * @param conn Database connection
     * @param sql SQL command
     * @throws SQLException
     * @see Connection
     */
    public static void execute(Connection conn, String sql) throws SQLException
    {
        execute(conn, sql, emptyObjArr);
    }

    /**
     * Executes SQL command
     * @param conn Database connection
     * @param sql SQL command
     * @param params SQL command parameters
     * @throws SQLException
     * @see Connection
     */
    public static void execute(Connection conn, String sql, Object... params) throws SQLException
    {
        logger.info(formatSqlExecute(sql, params));

        try(PreparedStatement stmt = conn.prepareStatement(sql))
        {
            fillStmtParams(stmt, params);
            stmt.execute();
        }
    }

    /**
     * Executes SQL command as a database update
     * @param conn Database connection
     * @param sql SQL command
     * @return Number of rows effected
     * @throws SQLException
     * @see Connection
     */
    public static int executeUpdate(Connection conn, String sql) throws SQLException
    {
        return executeUpdate(conn, sql, emptyObjArr);
    }

    /**
     * Executes SQL command as a database update
     * @param conn Database connection
     * @param sql SQL command
     * @param params SQL command parameters
     * @return Number of rows effected
     * @throws SQLException
     * @see Connection
     */
    public static int executeUpdate(Connection conn, String sql, Object... params) throws SQLException
    {
        logger.info(formatSqlExecute(sql, params));

        try(PreparedStatement stmt = conn.prepareStatement(sql))
        {
            fillStmtParams(stmt, params);
            return stmt.executeUpdate();
        }
    }

    /**
     * Executes SQL insertion commmand
     * @param conn Database connection
     * @param sql SQL command
     * @return The automatically generated primary key if there is one
     * @throws SQLException
     * @see Connection
     */
    public static int executeInsert(Connection conn, String sql) throws SQLException
    {
        return executeInsert(conn, sql, emptyObjArr);
    }

    /**
     * Executes SQL insertion commmand
     * @param conn Database connection
     * @param sql SQL command
     * @param params SQL command parameters
     * @return The automatically generated primary key if there is one
     * @throws SQLException
     * @see Connection
     */
    public static int executeInsert(Connection conn, String sql, Object... params) throws SQLException
    {
        try(PreparedStatement stmt = conn.prepareStatement(sql))
        {
            fillStmtParams(stmt, params);
            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys())
            {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Executes SQL query
     * @param conn Database connection
     * @param sql SQL command
     * @return CachedRowSet containing cached data from the returned ResultSet
     * @throws SQLException
     * @see Connection
     */
    public static CachedRowSet executeQuery(Connection conn, String sql) throws SQLException
    {
        return executeQuery(conn, sql, emptyObjArr);
    }

    /**
     * Executes SQL query
     * @param conn Database connection
     * @param sql SQL command
     * @param params SQL command parameters
     * @return CachedRowSet containing cached data from the returned ResultSet
     * @throws SQLException
     * @see Connection
     */
    public static CachedRowSet executeQuery(Connection conn, String sql, Object... params) throws SQLException
    {
        logger.info(formatSqlExecute(sql, params));

        try(PreparedStatement stmt = conn.prepareStatement(sql))
        {
            fillStmtParams(stmt, params);

            try(ResultSet rs = stmt.executeQuery())
            {
                CachedRowSet crs = rowSetFac.createCachedRowSet();
                crs.populate(rs);

                return crs;
            }
        }
    }

    /**
     * Sets the parameters for a PreparedStatement
     * @param stmt PreparedStatement to have its parameters set
     * @param params Parameters for the PreparedStatement
     * @throws SQLException
     * @see PreparedStatement
     */
    private static void fillStmtParams(PreparedStatement stmt, Object[] params) throws SQLException
    {
        for(int i = 0; i < params.length; ++i)
        {
            stmt.setObject(i+1, params[i]);
        }
    }

    /**
     * Formats a SQL command
     * @param sql SQL command that was executed
     * @param params Parameters for the SQL command
     * @return Formatted string for SQL command
     */
    public static String formatSqlExecute(String sql, Object... params)
    {
        return String.format("Executing SQL... \n\t SQL Command: %s \n\t Parameters: %s", sql, Arrays.toString(params));
    }
}
