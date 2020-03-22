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
import java.util.Optional;

public class Stock extends DatabaseItem
{
    private static final Logger logger = LoggerFactory.getLogger(Stock.class);

    private String symbol;
    private BigDecimal currPrice;
    private final Lazy<StockData> stockData;

    protected Stock(int id, String symbol, BigDecimal currPrice, int stockData)
    {
        super(id);
        this.symbol = symbol;
        this.currPrice = currPrice;
        this.stockData = new Lazy<>(() -> StockData.Find(stockData).orElse(null));
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

    // Search database for stock entry based on param
    public static Optional<Stock> Find(int id)
    {
        return find("id", id);
    }
    public static Optional<Stock> Find(String symbol)
    {
        return find("symbol", symbol);
    }

    /**
     * Find an existing stock in the database
     * @param searchCol Column to use in the `where` clause of the search
     * @param colValue Value to search for `where` clause of the search
     * @return Stock instance if found, otherwise empty if not
     */
    private static Optional<Stock> find(String searchCol, Object colValue)
    {
        logger.info("Searching for stock...");
        try(Connection conn = StockDatabase.getConnection();
            CachedRowSet crs = SqlCmd.executeQuery(conn, String.format("SELECT id, symbol, curr_price, data_id FROM stocks WHERE %s = ?", searchCol), colValue)
            )
        {
            // Return empty if nothing was found
            if(!crs.next()) return Optional.empty();

            return Optional.of(
                    new Stock(
                            crs.getInt("id"),
                            crs.getString("symbol"),
                            crs.getBigDecimal("curr_price"),
                            crs.getInt("data_id")
                    )
            );
        }
        catch(SQLException e)
        {
            logger.error(String.format("Unable to retrieve stock from database with search parameters %s = %s\n", searchCol, colValue), e);
        }
        return Optional.empty();
    }

    /**
     * Create a new stock and stock data in the database
     * @param symbol Symbol for the new stock
     * @param currPrice Current price for the new stock
     * @param stockData Data for the new stock
     * @return Stock instance of the newly created
     */
    private static Optional<Stock> Create(String symbol, BigDecimal currPrice, String stockData)
    {
        try(Connection conn = StockDatabase.getConnection())
        {
            logger.info("Creating new stock...");

            Optional<StockData> data = StockData.Create(stockData);
            if(!data.isPresent()) return Optional.empty();

            int id = SqlCmd.executeInsert(conn, "INSERT INTO stocks(symbol, curr_price, data_id) VALUES(?, ?, ?)", symbol, currPrice, data.get().getId());

            return Optional.of(new Stock(id, symbol, currPrice, data.get().getId()));
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
            SqlCmd.executeUpdate(conn, "UPDATE stocks SET symbol = ?, curr_price = ? WHERE id = ?", symbol, currPrice, id);
        }
    }
}
