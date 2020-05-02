package io.github.virtualstocksim.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Static methods for executing SQL
 */
public class SQL
{
    private static final Logger logger = LoggerFactory.getLogger(SQL.class);
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
     * Executes SQL insertion command
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
     * Executes SQL insertion command
     * @param conn Database connection
     * @param sql SQL command
     * @param params SQL command parameters
     * @return The automatically generated primary key if there is one
     * @throws SQLException
     * @see Connection
     */
    public static int executeInsert(Connection conn, String sql, Object... params) throws SQLException
    {
        logger.info(formatSqlExecute(sql, params));

        try(PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            fillStmtParams(stmt, params);
            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys())
            {
                //logger.info(String.valueOf(rs.getInt(1)));
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
     * Does the specified table exist
     * @param conn Database connection
     * @param table Database table
     * @return If the table exists in the database
     * @throws SQLException
     */
    public static boolean tableExists(Connection conn, String table) throws SQLException
    {
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs = metaData.getTables(null, "APP", table.toUpperCase(), null);

        return rs.next() && rs.getString(3).equals(table.toUpperCase());
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

    /**
     * Creates a map of table names in a result set
     * Useful for checking if a column exists in the result
     * @param rsmd The ResultSetMetaData
     * @return Map of column names
     * @throws SQLException
     */
    public static Map<String, Void> GetColumnNameMap(ResultSetMetaData rsmd) throws SQLException
    {
        // HashMap of column names returned in result
        HashMap<String, Void> columns = new HashMap<>();
        for(int i = 1; i <= rsmd.getColumnCount(); ++i)
        {
            columns.put(rsmd.getColumnName(i).toLowerCase(), null);
        }

        return columns;
    }

    /**
     *
     * @return Timestamp of current Instant
     */
    public static Timestamp GetTimeStamp()
    {
        return Timestamp.from(Instant.now());
    }
}
