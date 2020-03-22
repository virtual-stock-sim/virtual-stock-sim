package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.DatabaseItem;
import io.github.virtualstocksim.database.SqlCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class StockData extends DatabaseItem
{
    private static final Logger logger = LoggerFactory.getLogger(StockData.class);

    // Table columns
    private String data;
    protected StockData(int id, String data)
    {
        super(id);
        this.data = data;
    }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    static Optional<StockData> Find(int id)
    {
        return find("id", id);
    }

    // Search database for entry based on id
    private static Optional<StockData> find(String searchCol, Object colValue)
    {
        logger.info("Searching for stock data...");
        try(Connection conn = StockDatabase.getConnection();
            CachedRowSet crs = SqlCmd.executeQuery(conn, String.format("SELECT data FROM stocks_data WHERE %s = ?", searchCol), colValue)
        )
        {

            // Return empty if nothing was found
            if(!crs.next()) return Optional.empty();

            return Optional.of(new StockData(
                    crs.getInt("id"),
                    crs.getString("data")
            ));
        }
        catch (SQLException e)
        {
            logger.error(String.format("Unable to retrieve stock data from database with search parameters %s = %s\n", searchCol, colValue), e);
        }
        return Optional.empty();
    }

    static Optional<StockData> Create(String data)
    {
        logger.info("Creating new stock data...");

        try(Connection conn = StockDatabase.getConnection())
        {
            int id = SqlCmd.executeInsert(conn, "INSERT INTO stocks_data(data) VALUES(?)", data);
            return Optional.of(new StockData(id, data));
        }
        catch (SQLException e)
        {
            logger.error("Stock data creation failed\n",e);
            return Optional.empty();
        }
    }

    @Override
    public void commit() throws SQLException
    {
        logger.info("Committing stock data changes to database");

        try(Connection conn = StockDatabase.getConnection())
        {
            SqlCmd.executeUpdate(conn, "UPDATE stocks_data SET data = ? WHERE id = ?", data, id);
        }
    }
}
