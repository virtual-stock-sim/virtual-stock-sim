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

    public String symbol;
    public BigDecimal currPrice;
    public final Lazy<StockData> stockData;

    public Stock(int id, String symbol, BigDecimal currPrice, int stockData)
    {
        super(id);
        this.symbol = symbol;
        this.currPrice = currPrice;
        this.stockData = Lazy.lazily(() -> StockData.GetStockData(stockData).orElse(null));
    }

    // Search database for stock entry based on param
    public static Optional<Stock> GetStock(int id)
    {
        return get("SELECT id, symbol, curr,_price, data_id FROM stocks WHERE id = ?", id);
    }
    public static Optional<Stock> GetStock(String symbol)
    {
        return get("SELECT id, symbol, curr_price, data_id FROM stocks WHERE symbol = ?", symbol);
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
