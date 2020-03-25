package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.DatabaseItem;
import io.github.virtualstocksim.database.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class Stock extends DatabaseItem
{
    private static final Logger logger = LoggerFactory.getLogger(Stock.class);

    private String symbol;
    private BigDecimal currPrice;
    private BigDecimal prevClose;
    private int currVolume;
    private int prevVolume;
    private int stockDataId;
    private Timestamp lastUpdated;

    protected Stock(
            int id,
            String symbol,
            BigDecimal currPrice,
            BigDecimal prevClose,
            int currVolume,
            int prevVolume,
            int stockDataId,
            Timestamp lastUpdated
    )
    {
        super(id);
        this.symbol = symbol;
        this.currPrice = currPrice;
        this.prevClose = prevClose;
        this.currVolume = currVolume;
        this.prevVolume = prevVolume;
        this.stockDataId = stockDataId;
        this.lastUpdated = lastUpdated;
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getCurrPrice() { return currPrice; }
    public void setCurrPrice(BigDecimal currPrice) { this.currPrice = currPrice; }

    public BigDecimal getPrevClose() { return prevClose; }
    public void setPrevClose(BigDecimal prevClose) { this.prevClose = prevClose; }

    public int getCurrVolume() { return currVolume; }
    public void setCurrVolume(int currVolume) { this.currVolume = currVolume; }

    public int getPrevVolume() { return prevVolume; }
    public void setPrevVolume(int prevVolume) { this.prevVolume = prevVolume; }

    public int getStockDataId() { return this.stockDataId; }
    public void setStockDataId(int stockDataId) { this.stockDataId = stockDataId; }

    public Timestamp getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Timestamp lastUpdated) { this.lastUpdated = lastUpdated; }

    // Search database for stock entry based on param
    public static Optional<Stock> Find(int id)
    {
        return Find("id", id);
    }
    public static Optional<Stock> Find(String symbol)
    {
        return Find("symbol", symbol);
    }

    /**
     * Find an existing stock in the database
     * @param key Column to use in the `where` clause of the search
     * @param value Value to search for `where` clause of the search
     * @return Stock instance if found, otherwise empty if not
     */
    public static Optional<Stock> Find(String key, Object value)
    {
        List<Stock> stocks = FindCustom(String.format("SELECT id, symbol, curr_price, prev_close, curr_volume, prev_volume, data_id, last_updated FROM stocks WHERE %s = ?", key), value);
        if (stocks.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(stocks.get(0));
        }
    }

    /**
     * Search for one or more stocks with a custom SQL command.
     * Any empty fields are set to null or -1
     * Query MUST include a returned ID
     * @param sql SQL command
     * @param params SQL command parameters
     * @return List of Stock instances
     */
    public static List<Stock> FindCustom(String sql, Object... params)
    {
        logger.info("Searching for stock(s)...");
        try(Connection conn = StockDatabase.getConnection();
            CachedRowSet crs = SQL.executeQuery(conn, sql, params)
        )
        {
            List<Stock> stocks = new ArrayList<>(crs.size());

            ResultSetMetaData rsmd = crs.getMetaData();

            // HashMap of column names returned in result
            HashMap<String, Void> columns = new HashMap<>();
            for(int i = 1; i <= rsmd.getColumnCount(); ++i)
            {
                columns.put(rsmd.getColumnName(i).toLowerCase(), null);
            }

            // Make sure that the query returned an ID
            if(!columns.containsKey("id"))
            {
                throw new SQLException("Query must return ID");
            }

            // Iterate through all returned stocks and create a stock instance for each
            while(crs.next())
            {
                stocks.add(
                        new Stock(
                                crs.getInt("id"),
                                columns.containsKey("symbol")           ? crs.getString("symbol")           : null,
                                columns.containsKey("curr_price")       ? crs.getBigDecimal("curr_price")   : null,
                                columns.containsKey("prev_close")       ? crs.getBigDecimal("prev_close")   : null,
                                columns.containsKey("curr_volume")      ? crs.getInt("curr_volume")         : -1,
                                columns.containsKey("prev_volume")      ? crs.getInt("prev_volume")         : -1,
                                columns.containsKey("data_id")          ? crs.getInt("data_id")             : -1,
                                columns.containsKey("last_updated")     ? crs.getTimestamp("last_updated")  : null
                        )
                );
            }

            return stocks;
        }
        catch (SQLException e)
        {
            logger.error("Exception occurred while finding stock(s) in database\n", e);
        }

        return Collections.emptyList();
    }

    /**
     * Create a new stock
     * @param symbol Stock symbol
     * @param currPrice Current price
     * @param prevClose Previous closing price
     * @param currVolume Current market volume
     * @param prevVolume Previous market volume
     * @param stockDataId ID of referenced StockData
     * @return Stock instance of the newly created stock
     */
    private static Optional<Stock> Create(
            String symbol,
            BigDecimal currPrice,
            BigDecimal prevClose,
            int currVolume,
            int prevVolume,
            int stockDataId,
            Timestamp lastUpdated
    )
    {
        try(Connection conn = StockDatabase.getConnection())
        {
            logger.info("Creating new stock...");

            int id = SQL.executeInsert(conn, "INSERT INTO stocks(symbol, curr_price, prev_close, curr_volume, prev_volume, data_id, last_updated) VALUES(?, ?, ?, ?, ?, ?, ?)",
                    symbol, currPrice, prevClose, currVolume, prevVolume, stockDataId, lastUpdated);

            return Optional.of(new Stock(id, symbol, currPrice, prevClose, currVolume, prevVolume, stockDataId, lastUpdated));
        }
        catch (SQLException e)
        {
            logger.error("Stock creation failed\n", e);
            return Optional.empty();
        }
    }

    /**
     * Commit stock to stock database
     * @throws SQLException
     */
    @Override
    public void update() throws SQLException
    {
        try(Connection conn = StockDatabase.getConnection())
        {
            update(conn);
        }
    }

    /**
     * Commit Stock to database
     * @param conn Connection to stock database
     * @throws SQLException
     */
    @Override
    public void update(Connection conn) throws SQLException
    {
        logger.info(String.format("Committing stock changes to database for Stock ID %d", id));

        List<String> updated = new LinkedList<>();
        List<Object> params = new LinkedList<>();

        // Map of column names and values
        Map<String, Object> columns = new HashMap<>();
        columns.put("symbol", symbol);
        columns.put("curr_price", currPrice);
        columns.put("prev_close", prevClose);
        columns.put("curr_volume", currVolume);
        columns.put("prev_volume", prevVolume);
        columns.put("data_id", stockDataId);
        columns.put("last_updated", lastUpdated);

        // Check each column name and add it to the update list if its been updated
        for(Map.Entry<String, Object> c : columns.entrySet())
        {
            if(c.getValue() != null)
            {
                updated.add(c.getKey() + " = ?");
                params.add(c.getValue());
            }
        }

        if(updated.isEmpty())
        {
            logger.warn(String.format("Abandoning update for Stock ID %d; Nothing to update", id));
        }
        else
        {
            params.add(id);
            SQL.executeUpdate(conn, String.format("UPDATE stocks SET %s WHERE id = ?", String.join(", ", updated)), params.toArray());
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
        logger.info(String.format("Removing Stock with ID %d from database", id));
        SQL.executeUpdate(conn, "DELETE FROM stocks WHERE id = ?", id);
    }
}
