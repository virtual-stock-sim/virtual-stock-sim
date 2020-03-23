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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        List<Stock> stocks = CustomFind(String.format("SELECT id, symbol, curr_price, data_id, last_updated FROM stocks WHERE %s = ?", key), value);
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
     * Search for one or more stocks with a custom SQL command
     * @param sql SQL command
     * @param params SQL command parameters
     * @return List of Stock instances
     */
    public static List<Stock> CustomFind(String sql, Object... params)
    {
        logger.info("Searching for stock(s)...");
        try(Connection conn = StockDatabase.getConnection();
            CachedRowSet crs = SqlCmd.executeQuery(conn, sql, params);
        )
        {
            List<Stock> stocks = new ArrayList<>(crs.size());

            while(crs.next())
            {
                stocks.add(
                        new Stock(
                                crs.getInt("id"),
                                crs.getString("symbol"),
                                crs.getBigDecimal("curr_price"),
                                crs.getInt("data_id"),
                                crs.getTimestamp("last_updated")
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

    @Override
    public void commit() throws SQLException
    {
        if(stockData.hasEvaluated()) getStockData().commit();

        logger.info("Committing stock changes to database");

        try(Connection conn = StockDatabase.getConnection())
        {
            SqlCmd.executeUpdate(conn, "UPDATE stocks SET symbol = ?, curr_price = ?, last_updated = ? WHERE id = ?", symbol, currPrice, id, lastUpdated);
        }
    }
}
