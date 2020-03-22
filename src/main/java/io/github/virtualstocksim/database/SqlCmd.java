package io.github.virtualstocksim.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Static methods for executing SQL
 */
public class SqlCmd
{
    private static final Logger logger = LoggerFactory.getLogger(SqlCmd.class);

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

        PreparedStatement stmt = conn.prepareStatement(sql);
        fillStmtParams(stmt, params);
        stmt.execute();
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

        PreparedStatement stmt = conn.prepareStatement(sql);
        fillStmtParams(stmt, params);
        return stmt.executeUpdate();
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
        logger.info(formatSqlExecute(sql, params));

        PreparedStatement stmt = conn.prepareStatement(sql);
        fillStmtParams(stmt, params);
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        return rs.next() ? rs.getInt(1) : 0;
    }

    /**
     * Executes SQL query
     * @param conn Database connection
     * @param sql SQL command
     * @param <T> Type for the first column returned by the query.
     *           To be used for the key for the returned TreeMap
     * @return TreeMap of results. Key is value of first column returned by query, Value is HashMap of row contents with column name as Key
     * @throws SQLException
     * @see Connection
     * @see TreeMap
     */
    public static <T extends Comparable<T>> TreeMap<T, HashMap<String, Object>> executeQuery(Connection conn, String sql) throws SQLException
    {
        return executeQuery(conn, sql, emptyObjArr);
    }

    /**
     * Executes SQL query
     * @param conn Database connection
     * @param sql SQL command
     * @param params SQL command parameters
     * @param <T> Type for the first column returned by the query.
     *           To be used for the key for the returned TreeMap
     * @return TreeMap of results. Key is value of first column returned by query, Value is HashMap of row contents with column name as Key
     * @throws SQLException
     * @see Connection
     * @see TreeMap
     */
    public static <T extends Comparable<T>> TreeMap<T, HashMap<String, Object>> executeQuery(Connection conn, String sql, Object... params) throws SQLException
    {
        logger.info(formatSqlExecute(sql, params));

        PreparedStatement stmt = conn.prepareStatement(sql);
        fillStmtParams(stmt, params);

        ResultSet rs = stmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();

        TreeMap<T, HashMap<String, Object>> results = new TreeMap<>();

        // Iterate over each row of the result set
        while(rs.next())
        {
            // New hash map to store row contents
            HashMap<String, Object> row = new HashMap<>();
            // Iterate over each column of row
            for(int i = 1; i <= rsmd.getColumnCount(); ++i)
            {
                // Use column name as key for column value
                row.put(rsmd.getColumnName(i), rs.getObject(i));
            }

            // Add row to results TreeMap with value of first column as the key
            results.put((T) rs.getObject(1), row);
        }

        return results;
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
