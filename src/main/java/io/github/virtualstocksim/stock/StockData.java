package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.DatabaseException;
import io.github.virtualstocksim.database.DatabaseItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class StockData extends DatabaseItem
{

    private static final Logger logger = LoggerFactory.getLogger(StockData.class);
    private static StockCache cache = StockCache.Instance();

    // Table columns
    private String data;
    protected StockData(int id, String data)
    {
        super(id);
        this.data = data;
    }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    // Search database for entry based on id
    static Optional<StockData> Find(int id)
    {
        try
        {
            logger.info("Searching for stock data...");
            ResultSet rs = cache.executeQuery("SELECT data FROM stocks_data WHERE id=?", id);

            // Return empty if nothing was found
            if(!rs.next()) return Optional.empty();

            return Optional.of(new StockData(
                    id,
                    rs.getString("data")
            ));
        }
        catch (DatabaseException e)
        {
            logger.error(String.format("Stock Data with id %d not found\n", id), e);
        }
        catch(SQLException e)
        {
            logger.error("Error while parsing result from stock cache\n", e);
        }
        return Optional.empty();
    }

    static Optional<StockData> Create(String data)
    {
        try
        {
            logger.info("Creating new stock data...");
            int id = cache.executeInsert("INSERT INTO stocks_data(data) VALUES(?)", data);
            return Optional.of(new StockData(id, data));
        }
        catch (DatabaseException e)
        {
            logger.error("",e);
            return Optional.empty();
        }
    }

    @Override
    public void commit() throws DatabaseException
    {
        logger.info("Committing stock data changes to database");
        cache.executeUpdate("UPDATE stocks_data SET data = ? WHERE id = ?",
                data,
                id
                );
    }
}
