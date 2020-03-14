package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.DatabaseItem;
import io.github.virtualstocksim.util.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Stock extends DatabaseItem
{
    private static final Logger logger = LoggerFactory.getLogger(Stock.class);
    private static StockCache dataCache = StockCache.Instance();

    private String symbol;
    private BigDecimal currPrice;
    private final Lazy<StockData> stockData;

    public Stock(int id, String symbol, BigDecimal currPrice, int stockData)
    {
        super(id);
        this.symbol = symbol;
        this.currPrice = currPrice;
        this.stockData = Lazy.lazily(() -> StockData.GetStockData(stockData).orElse(null));
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getCurrPrice() { return currPrice; }
    public void setCurrPrice(BigDecimal currPrice) { this.currPrice = currPrice; }

    public String getStockData() { return stockData.get().getData(); }

    // Search database for stock entry based on param
    public static Optional<Stock> GetStock(int id)
    {
        // Hardcoded values for testing
        switch (id)
        {
            case 1: return Optional.of(new Stock(id, "TSLA", new BigDecimal("360.00"), id));
            case 2: return Optional.of(new Stock(id, "F", new BigDecimal("17.00"), id));
            case 3: return Optional.of(new Stock(id, "DD", new BigDecimal("123.00"), id));
            case 4: return Optional.of(new Stock(id, "AAPL", new BigDecimal("400.00"), id));
            case 5: return Optional.of(new Stock(id, "GOOGL", new BigDecimal("51.30"), id));
            default:
                return Optional.empty();
        }

        //return get("SELECT id, symbol, curr,_price, data_id FROM stocks WHERE id = ?", id);
    }
    public static Optional<Stock> GetStock(String symbol)
    {
        // Hardcoded values for jsp testing
        switch (symbol)
        {
            case "TSLA": return Optional.of(new Stock(1, "TSLA", new BigDecimal("360.00"), 1));
            case "F": return Optional.of(new Stock(2, "F", new BigDecimal("17.00"), 2));
            case "DD": return Optional.of(new Stock(3, "DD", new BigDecimal("123.00"), 3));
            case "AAPL": return Optional.of(new Stock(4, "AAPL", new BigDecimal("400.00"), 4));
            case "GOOGL": return Optional.of(new Stock(5, "GOOGL", new BigDecimal("51.30"), 5));
            default:
                return Optional.empty();
        }

        //return get("SELECT id, symbol, curr_price, data_id FROM stocks WHERE symbol = ?", symbol);
    }

    // Generic search func
    private static Optional<Stock> get(String sql, Object param)
    {
        try(
                ResultSet rs = dataCache.executeQuery(sql, param)
        )
        {
            // Return empty if there are no entries
            if(!rs.next()) return Optional.empty();

            return Optional.of(
                    new Stock(
                            rs.getInt("id"),
                            rs.getString("symbol"),
                            rs.getBigDecimal("curr_price"),
                            rs.getInt("data_id")
                    )
            );
        }
        catch(SQLException e)
        {
            logger.error(e.getMessage());
            logger.warn(String.format("Stock with search parameter %s not found", param));
            return Optional.empty();
        }
    }

    @Override
    public void commit()
    {

    }
}
