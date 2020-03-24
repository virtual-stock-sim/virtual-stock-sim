package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.DatabaseItem;
import io.github.virtualstocksim.database.SqlCmd;
import io.github.virtualstocksim.util.Lazy;
import org.apache.commons.lang3.concurrent.ConcurrentException;
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
    private final Lazy<StockData> stockData;
    private Timestamp lastUpdated;

    protected Stock(int id, String symbol, BigDecimal currPrice, int stockData, Timestamp lastUpdated)
    {
        super(id);
        this.symbol = symbol;
        this.currPrice = currPrice;
        this.stockData = new Lazy<>(() -> StockData.Find(stockData).orElse(null));
        this.lastUpdated = lastUpdated;
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getCurrPrice() { return currPrice; }
    public void setCurrPrice(BigDecimal currPrice) { this.currPrice = currPrice; }

    public StockData getStockData()
    {
        try
        {
            return stockData.get();
        }
        catch (ConcurrentException e)
        {
            logger.error("Error accessing StockData\n", e);
        }
        return null;
    }

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
        List<Stock> stocks = FindCustom(String.format("SELECT id, symbol, curr_price, data_id, last_updated FROM stocks WHERE %s = ?", key), value);
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
            CachedRowSet crs = SqlCmd.executeQuery(conn, sql, params)
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
                                columns.containsKey("symbol") ? crs.getString("symbol") : null,
                                columns.containsKey("curr_price") ? crs.getBigDecimal("curr_price") : null,
                                columns.containsKey("data_id") ? crs.getInt("data_id") : -1,
                                columns.containsKey("last_updated") ? crs.getTimestamp("last_updated") : null
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
     * Create a new stock and stock data in the database
     * @param symbol Symbol for the new stock
     * @param currPrice Current price for the new stock
     * @param stockData Data for the new stock
     * @return Stock instance of the newly created
     */
    private static Optional<Stock> Create(String symbol, BigDecimal currPrice, String stockData, Timestamp lastUpdated)
    {
        try(Connection conn = StockDatabase.getConnection())
        {
            logger.info("Creating new stock...");

            // Find associated StockData
            Optional<StockData> data = StockData.Create(stockData, lastUpdated);
            if(!data.isPresent()) return Optional.empty();

            int id = SqlCmd.executeInsert(conn, "INSERT INTO stocks(symbol, curr_price, data_id, last_updated) VALUES(?, ?, ?, ?)", symbol, currPrice, data.get().getId(), lastUpdated);

            return Optional.of(new Stock(id, symbol, currPrice, data.get().getId(), lastUpdated));
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
        if(stockData.hasEvaluated()) getStockData().update();

        logger.info(String.format("Committing stock changes to database for Stock ID %d", id));

        List<String> comitted = new LinkedList<>();
        List<String> omitted = new LinkedList<>();
        List<Object> params = new LinkedList<>();

        // Add symbol to update
        if(symbol != null && !symbol.trim().isEmpty())
        {
            comitted.add("symbol = ?");
            params.add(symbol);
        }
        else
        {
            omitted.add("symbol");
        }

        // Add current price to update
        if(currPrice != null)
        {
            comitted.add("curr_price = ?");
            params.add(currPrice);
        }
        else
        {
            omitted.add("curr_price");
        }

        // Add last updated to update
        if(lastUpdated != null)
        {
            comitted.add("last_updated = ?");
            params.add(lastUpdated);
        }
        else
        {
            omitted.add("last_updated");
        }

        if(comitted.isEmpty())
        {
            logger.warn(String.format("Abandoning commit for Stock ID %d; Nothing to commit", id));
        }
        else
        {
            if(!omitted.isEmpty())
            {
                logger.info(String.format("Omitting the following empty columns from commit:\n%s", String.join(", ", omitted)));
            }

            params.add(id);
            SqlCmd.executeUpdate(conn, String.format("UPDATE stocks SET %s WHERE id = ?", String.join(", ", comitted)), params.toArray());
        }
    }

    @Override
    public void delete() throws SQLException
    {

    }

    @Override
    public void delete(Connection conn) throws SQLException
    {
        logger.info(String.format("Removing Stock with ID %d from database", id));
        SqlCmd.executeUpdate(conn, "DELETE FROM stocks WHERE id = ?", id);
    }
}
