package io.github.virtualstocksim.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;

// Generic database controller
public class Database
{
    protected Connection db;
    private static final Logger defaultLogger = LoggerFactory.getLogger(Database.class);
    private Logger logger;
    protected String dbPath;

    /**
     * @param dbPath Path to database to open connection to
     * @throws DatabaseException Unable to close connection
     */
    public Database(String dbPath) throws DatabaseException
    {
        this(dbPath, defaultLogger);
    }

    /**
     * @param dbPath Path to database to open connection to
     * @param logger Logger to use when logging SQL commands
     *               Allowing a custom logger allows classes that extend database
     *               to use their own logger so its more explicit what class
     *               is doing what
     * @throws DatabaseException Unable to close database connection
     */
    public Database(String dbPath, Logger logger) throws DatabaseException
    {
        this.logger = logger;
        changeDB(dbPath);
    }

    public String getDbPath() { return this.dbPath; }

    public void closeConn() throws DatabaseException
    {
        try
        {
            if(db != null && !db.isClosed())
            {
                db.close();
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Unable to close connection", dbPath, e);
        }
    }

    /**
     * Changes the connected database
     * @param dbPath Path for the new database
     * @throws DatabaseException Failed to open new database connection
     */
    public void changeDB(String dbPath) throws DatabaseException
    {
        closeConn();
        try
        {
            db = DriverManager.getConnection(String.format("jdbc:derby:%s;create=true", dbPath));
            this.dbPath = dbPath;
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Failed to open new connection", dbPath, e);
        }
    }

    /**
     * Checks if a table exists within the database schema
     * @param table Name of the table
     * @return If the table exists
     * @throws DatabaseException Failed to check table existence
     */
    public boolean tableExists(String table) throws DatabaseException
    {
        try
        {
            logger.info(String.format("Checking if table %s exists in %s", table, dbPath));
            DatabaseMetaData metaData = db.getMetaData();
            ResultSet rs = metaData.getTables(null, "APP", table.toUpperCase(), null);

            return rs.next() && rs.getString(3).equals(table.toUpperCase());
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Error checking table existence", dbPath, e);
        }
    }

    /**
     * Create a table in the database
     * @param name Name of the table
     * @param columns Columns of the table (i.e. `id INT NOT NULL`)
     * @throws DatabaseException Create table SQL execution failure
     */
    public void createTable(String name, String... columns) throws DatabaseException
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

    /**
     * Executes a SQL command
     * @param sql SQL command
     * @throws DatabaseException SQL Execution Failure
     */    public void executeStmt(String sql) throws DatabaseException
    {
        executeStmt(sql, emptyObjArr);
    }

    /**
     * Executes a SQL command
     * @param sql SQL command
     * @param params Parameters for SQL command
     * @throws DatabaseException SQL Execution Failure
     */
    public void executeStmt(String sql, Object... params) throws DatabaseException
    {
        try
        {
            logger.info(formatSqlExecute(sql, params));
            PreparedStatement stmt = db.prepareStatement(sql);
            for(int i = 0; i < params.length; ++i)
            {
                stmt.setObject(i, params[i]);
            }
            stmt.execute();
            logger.info("Execution successful");
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Execution Failure", dbPath, e);
        }
    }

    /**
     * Executes given SQL as a query
     * @param sql SQL query
     * @return Results from query
     * @throws DatabaseException SQL Query Failure
     */
    public ResultSet executeQuery(String sql) throws DatabaseException
    {
        return executeQuery(sql, emptyObjArr);
    }

    /**
     * Executes given SQL as a query
     * @param sql SQL query
     * @param params Parameters for SQL query
     * @return Results from query
     * @throws DatabaseException SQL Query Failure
     */
    public ResultSet executeQuery(String sql, Object... params) throws DatabaseException
    {
        try
        {
            logger.info(formatSqlExecute(sql, params));
            PreparedStatement stmt = db.prepareStatement(sql);
            for(int i = 0; i < params.length; ++i)
            {
                stmt.setObject(i+1, params[i]);
            }
            ResultSet rs = stmt.executeQuery();
            logger.info("Query Successful");
            return rs;
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Query Failure", dbPath, e);
        }
    }

    /**
     * Executes given SQL as an update
     * @param sql SQL update command
     * @return Number of rows effected by update
     * @throws DatabaseException SQL Update Failure
     */
    public int executeUpdate(String sql) throws DatabaseException
    {
        return executeUpdate(sql, emptyObjArr);
    }

    /**
     * Executes given SQL as an update
     * @param sql SQL update command
     * @param params Parameters for the SQL command
     * @return Number of rows effected by update
     * @throws DatabaseException SQL Update Failure
     */
    public int executeUpdate(String sql, Object... params) throws DatabaseException
    {
        try
        {
            logger.info(formatSqlExecute(sql, params));
            PreparedStatement stmt = db.prepareStatement(sql);
            for(int i = 0; i < params.length; ++i)
            {
                stmt.setObject(i+1, params[i]);
            }
            int effected = stmt.executeUpdate();
            logger.info("Update successful");
            return effected;
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Update Failure", dbPath, e);
        }
    }

    /**
     * Executes given SQL as an insertion
     * @param sql SQL insertion command
     * @return The automatically generated primary key if it exists, otherwise 0
     * @throws DatabaseException SQL Insertion Failure
     */
    public int executeInsert(String sql) throws DatabaseException
    {
        return executeInsert(sql, emptyObjArr);
    }

    /**
     * Executes given SQL as an insertion
     * @param sql SQL insertion command
     * @param params Parameters for the SQL command
     * @return The automatically generated primary key if it exists, otherwise 0
     * @throws DatabaseException SQL Insertion Failure
     */
    public int executeInsert(String sql, Object... params) throws DatabaseException
    {
        try
        {
            logger.info(formatSqlExecute(sql, params));
            PreparedStatement stmt = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for(int i = 0; i < params.length; ++i)
            {
                stmt.setObject(i+1, params[i]);
            }
            stmt.executeUpdate();
            logger.info("Insertion successful");

            logger.info("Getting generated key from insertion...");
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
        catch (SQLException e)
        {
            throw new DatabaseException("Insert Failure", dbPath, e);
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
