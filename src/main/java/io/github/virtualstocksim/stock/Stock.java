package io.github.virtualstocksim.stock;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.github.virtualstocksim.database.DatabaseItem;
import io.github.virtualstocksim.database.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

public class Stock extends DatabaseItem
{
    private static final Logger logger = LoggerFactory.getLogger(Stock.class);

    private String symbol;
    private BigDecimal currPrice;
    private BigDecimal prevClose;
    private Integer currVolume;
    private Integer prevVolume;
    private Integer stockDataId;
    private Timestamp lastUpdated;

    protected Stock(
            int id,
            String symbol,
            BigDecimal currPrice,
            BigDecimal prevClose,
            Integer currVolume,
            Integer prevVolume,
            Integer stockDataId,
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

    /**
     * Creates a new stock object to act as a data container. This does not represent database data
     * and will fail to commit to the database as it has an id of -1
     * @param symbol Stock symbol
     * @param currPrice Current price
     * @param prevClose Previous closing price
     * @param currVolume Current market volume
     * @param prevVolume Previous market volume
     */
    public Stock(String symbol, BigDecimal currPrice, BigDecimal prevClose, Integer currVolume, Integer prevVolume)
    {
        this(-1, symbol, currPrice, prevClose, currVolume, prevVolume, null, null);
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getCurrPrice() { return currPrice; }
    public void setCurrPrice(BigDecimal currPrice) { this.currPrice = currPrice; }

    public BigDecimal getPrevClose() { return prevClose; }
    public void setPrevClose(BigDecimal prevClose) { this.prevClose = prevClose; }

    public Integer getCurrVolume() { return currVolume; }
    public void setCurrVolume(Integer currVolume) { this.currVolume = currVolume; }

    public Integer getPrevVolume() { return prevVolume; }
    public void setPrevVolume(Integer prevVolume) { this.prevVolume = prevVolume; }

    public Integer getStockDataId() { return this.stockDataId; }
    public void setStockDataId(Integer stockDataId) { this.stockDataId = stockDataId; }

    public Timestamp getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Timestamp lastUpdated) { this.lastUpdated = lastUpdated; }

    public AbstractMap.SimpleEntry<String, Double> getSymbolAndPercentChange(){
        return new AbstractMap.SimpleEntry<>(this.symbol,this.getPercentChange());
    }

    // Search database for stock entry based on param
    public static Optional<Stock> Find(Integer id)
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
        List<Stock> stocks = FindCustom(String.format("SELECT id, symbol, curr_price, prev_close, curr_volume, prev_volume, data_id, last_updated FROM stock WHERE %s = ?", key), value);
        if (stocks.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(stocks.get(0));
        }
    }

    public static List<Stock> FindAll()
    {
        return FindCustom("SELECT id, symbol, curr_price, prev_close, curr_volume, prev_volume, data_id, last_updated FROM stock");
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

            Map<String, Void> columns = SQL.GetColumnNameMap(crs.getMetaData());

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
                                columns.containsKey("curr_volume")      ? crs.getInt("curr_volume")         : null,
                                columns.containsKey("prev_volume")      ? crs.getInt("prev_volume")         : null,
                                columns.containsKey("data_id")          ? crs.getInt("data_id")             : null,
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
    public static Optional<Stock> Create(
            String symbol,
            BigDecimal currPrice,
            BigDecimal prevClose,
            Integer currVolume,
            Integer prevVolume,
            Integer stockDataId,
            Timestamp lastUpdated
    )
    {
        try(Connection conn = StockDatabase.getConnection())
        {
            logger.info("Creating new stock...");

            int id = SQL.executeInsert(conn, "INSERT INTO stock(symbol, curr_price, prev_close, curr_volume, prev_volume, data_id, last_updated) VALUES(?, ?, ?, ?, ?, ?, ?)",
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
     * @throws SQLException If there was an error while committing changes to the database
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
     * @throws SQLException If there was an error while committing changes to the database
     */
    @Override
    public void update(Connection conn) throws SQLException
    {
        if(id < 1)
        {
            throw new SQLException("Data container stocks cannot be committed to the database");
        }

        logger.info(String.format("Committing stock changes to database for Stock with ID %d and Symbol %s", id, symbol));

        List<String> updated = new LinkedList<>();
        List<Object> params = new LinkedList<>();

        // Map of column names and values
        Map<String, Object> columns = new HashMap<>();
        if(symbol != null && !symbol.trim().isEmpty())  columns.put("symbol",       symbol);
        if(currPrice != null)                           columns.put("curr_price",   currPrice);
        if(prevClose != null)                           columns.put("prev_close",   prevClose);
        if(currVolume != null)                          columns.put("curr_volume",  currVolume);
        if(prevVolume != null)                          columns.put("prev_volume",  prevVolume);
        if(stockDataId != null)                         columns.put("data_id",      stockDataId);
        if(lastUpdated != null)                         columns.put("last_updated", lastUpdated);

        // Check each column name and add it to the update list if its been updated
        for(Map.Entry<String, Object> c : columns.entrySet())
        {
            updated.add(c.getKey() + " = ?");
            params.add(c.getValue());
        }

        if(updated.isEmpty())
        {
            logger.warn(String.format("Abandoning update for Stock with ID %d and Symbol %s; Nothing to update", id, symbol));
        }
        else
        {
            params.add(id);
            SQL.executeUpdate(conn, String.format("UPDATE stock SET %s WHERE id = ?", String.join(", ", updated)), params.toArray());
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
        logger.info(String.format("Removing Stock with ID %d and Symbol %s from database", id, symbol));
        SQL.executeUpdate(conn, "DELETE FROM stock WHERE id = ?", id);
    }

    private static final BigDecimal DECIMAL_100 = new BigDecimal("100.0");
    public double getPercentChange()
    {
        if(currPrice != null && prevClose != null)
        {
            if(prevClose.compareTo(BigDecimal.ZERO) == 0)
            {
                return 100.0;
            }
            else
            {
                BigDecimal diff = currPrice.subtract(prevClose, MathContext.DECIMAL64);
                BigDecimal change = diff.divide(prevClose.abs(), 9, RoundingMode.HALF_EVEN);
                BigDecimal percentChange = change.multiply(DECIMAL_100);

                DecimalFormat df = new DecimalFormat("#.##");
                return Double.parseDouble(df.format(percentChange));
            }
        }
        else
        {
            throw new NullPointerException("Current price and/or previous closing prices are null");
        }
    }

    public JsonObject asJson()
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("symbol", symbol);
        obj.addProperty("currPrice", currPrice);
        obj.addProperty("prevClose", prevClose);
        if(currPrice != null && prevClose != null)
        {
            obj.addProperty("percentChange", getPercentChange());
        }
        else
        {
            obj.add("percentChange", JsonNull.INSTANCE);
        }
        obj.addProperty("currVolume", currVolume);
        obj.addProperty("prevVolume", prevVolume);
        if(lastUpdated != null)
        {
            obj.addProperty("lastUpdated", lastUpdated.toString());
        }
        else
        {
            obj.add("lastUpdated", JsonNull.INSTANCE);
        }

        return obj;
    }
}
