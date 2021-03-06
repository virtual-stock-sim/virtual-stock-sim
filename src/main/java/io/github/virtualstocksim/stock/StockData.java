package io.github.virtualstocksim.stock;

import com.google.gson.*;
import io.github.virtualstocksim.database.DatabaseItem;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.util.Result;
import io.github.virtualstocksim.util.json.JsonError;
import io.github.virtualstocksim.util.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class StockData extends DatabaseItem
{
    private static final Logger logger = LoggerFactory.getLogger(StockData.class);

    // Table columns
    private String data;
    private Timestamp lastUpdated;
    protected StockData(int id, String data, Timestamp lastUpdated)
    {
        super(id);
        this.data = data;
        this.lastUpdated = lastUpdated;
    }

    /**
     * Creates a new stock data object to act as a data container. This does not represent database data
     * and will fail to commit to the database as it has an id of -1
     * @param data Stock data
     */
    public StockData(String data)
    {
        this(-1, data, null);
    }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public Timestamp getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Timestamp lastUpdated) { this.lastUpdated = lastUpdated; }

    // Search database for stock data entry based on param
    public static Optional<StockData> Find(int id)
    {
        return Find("id", id);
    }
    public static Optional<StockData> Find(String key, Object value)
    {
        List<StockData> stockDatas = FindCustom(String.format("SELECT id, data, last_updated FROM stock_data WHERE %s = ?", key), value);

        if(stockDatas.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(stockDatas.get(0));
        }
    }
    public static Optional<StockData> Find(String symbol)
    {
        List<StockData> results =
                FindCustom("SELECT stock.data_id, stock_data.id, stock_data.data, stock_data.last_updated FROM stock, stock_data WHERE stock.symbol = ? AND stock.data_id = stock_data.id", symbol);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public static List<StockData> FindAll()
    {
        return FindCustom("SELECT id, data, last_updated FROM stock_data");
    }

    /**
     * Search for one or more stock datas with a custom SQL command
     * Any empty fields are set to null or -1
     * Query MUST include a returned ID
     * @param sql SQL command
     * @param params SQL command parameters
     * @return List of StockData instances
     */
    public static List<StockData> FindCustom(String sql, Object... params)
    {
        logger.info("Searching for stock data...");
        try(Connection conn = StockDatabase.getConnection();
            CachedRowSet crs = SQL.executeQuery(conn, sql, params)
        )
        {
            List<StockData> stockDatas = new ArrayList<>(crs.size());

            Map<String, Void> columns = SQL.GetColumnNameMap(crs.getMetaData());

            // Make sure query returned an ID
            if(!columns.containsKey("id"))
            {
                throw new SQLException("Query must return ID");
            }

            // Iterate through all returned stock datas and create a StockData instance for each one
            while(crs.next())
            {
                // Attempt to get clob
                String data = null;
                if(columns.containsKey("data"))
                {
                    Clob clob = crs.getClob("data");
                    if(clob.length() > 0)
                    {
                        data = clob.getSubString(1, (int) clob.length());
                    }
                }

                stockDatas.add(
                        new StockData(
                                crs.getInt("id"),
                                data,
                                columns.containsKey("last_updated") ? crs.getTimestamp("last_updated") : null
                        )
                );
            }

            return stockDatas;
        }
        catch (SQLException e)
        {
            logger.error("Exception occurred while finding stock data(s) in database\n", e);
        }
        return Collections.emptyList();
    }

    /**
     * Create a new stock data in the database
     * @param data Bulk stock data
     * @param lastUpdated Timestamp of when this was last updated
     * @return StockData instance of the newly created stock
     */
    public static Optional<StockData> Create(String data, Timestamp lastUpdated)
    {
        logger.info("Creating new stock data...");

        try(Connection conn = StockDatabase.getConnection())
        {
            int id = SQL.executeInsert(conn, "INSERT INTO stock_data(data, last_updated) VALUES(?, ?)", data, lastUpdated);
            return Optional.of(new StockData(id, data, lastUpdated));
        }
        catch (SQLException e)
        {
            logger.error("Stock data creation failed\n", e);
            return Optional.empty();
        }
    }

    @Override
    public void update() throws SQLException
    {
        try(Connection conn = StockDatabase.getConnection())
        {
            update(conn);
        }
    }

    @Override
    public void update(Connection conn) throws SQLException
    {
        if(id < 1)
        {
            throw new SQLException("Data container stocks cannot be committed to the database");
        }

        logger.info(String.format("Committing stock data changes to database for Stock data ID %d", id));

        List<String> updated = new LinkedList<>();
        List<Object> params = new LinkedList<>();
        HashMap<String, Object> columns = new HashMap<>();
        if(data != null && !data.trim().isEmpty())  columns.put("data", data);
        if(data != null)                            columns.put("last_updated", lastUpdated);

        // Map of column names and values
        for(Map.Entry<String, Object> c : columns.entrySet())
        {
            updated.add(c.getKey() + " = ?");
            params.add(c.getValue());
        }

        // Check each column name and add it to the update list if its been updated
        if(updated.isEmpty())
        {
            logger.warn(String.format("Abandoning update for Stock data ID %d; Nothing to update", id));
        }
        else
        {
            params.add(id);
            SQL.executeUpdate(conn, String.format("UPDATE stock_data SET %s WHERE id = ?", String.join(", ", updated)), params.toArray());
        }
    }

    @Override
    public void delete() throws SQLException
    {
        try(Connection conn = StockDatabase.getConnection())
        {
            delete(conn);
        }
    }

    @Override
    public void delete(Connection conn) throws SQLException
    {
        logger.info(String.format("Removing Stock data with ID %d from database", id));
        SQL.executeUpdate(conn, "DELETE FROM stocks WHERE id = ?", id);
    }


    public JsonObject asJson() throws JsonParseException
    {
        JsonElement parsedData = JsonParser.parseString(data);
        JsonObject dataObj = JsonUtil.getAs(parsedData, JsonElement::getAsJsonObject)
                                     .getOrNull(err ->
                                                {
                                                    logger.error("Stock data is corrupted: \n" + data);
                                                    throw new JsonParseException("Corrupted Stock data");
                                                });

        dataObj.addProperty("lastUpdated", lastUpdated.toString());
        return dataObj;
    }
}
