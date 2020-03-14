package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.DatabaseItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class StockData extends DatabaseItem
{

    private static final Logger logger = LoggerFactory.getLogger(StockData.class);
    private static StockCache dataCache = StockCache.Instance();

    // Table columns
    private String data;
    public StockData(int id, String data)
    {
        super(id);
        this.data = data;
    }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    // Search database for entry based on id
    public static Optional<StockData> GetStockData(int id)
    {
        try(
                ResultSet rs = dataCache.executeQuery("SELECT data FROM stocks_data WHERE id=?", id)
        )
        {
            if(!rs.next()) return Optional.empty();

            return Optional.of(new StockData(
                    id,
                    rs.getString("data")
            ));
        }
        catch(SQLException e)
        {
            logger.error(e.getMessage());
            logger.warn(String.format("StockData with ID of `%d` not found", id));
            return Optional.empty();
        }
    }

    @Override
    public void commit()
    {

    }
}
