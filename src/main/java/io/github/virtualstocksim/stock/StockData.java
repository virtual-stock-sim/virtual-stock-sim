package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.DatabaseItem;
import io.github.virtualstocksim.database.SqlCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StockData extends DatabaseItem
{
    private static final Logger logger = LoggerFactory.getLogger(StockData.class);

    // Table columns
    private String data;
    private Timestamp lastUpdated;
    protected StockData(int id, String data, Timestamp lastUpdated)
    {
        super(id);
        this.data = data;
        this.lastUpdated = lastUpdated;
    }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public Timestamp getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Timestamp lastUpdated) { this.lastUpdated = lastUpdated; }

    static Optional<StockData> Find(int id)
    {
        return Find("id", id);
    }
    static Optional<StockData> Find(String key, Object value)
    {
        List<StockData> stockDatas = FindCustom(String.format("SELECT id, data, last_updated FROM stocks_data WHERE %s = ?", key), value);

        if(stockDatas.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(stockDatas.get(0));
        }
    }

    // Search database for entry based on id
    static List<StockData> FindCustom(String sql, Object... params)
    {
        logger.info("Searching for stock data...");
        try(Connection conn = StockDatabase.getConnection();
            CachedRowSet crs = SqlCmd.executeQuery(conn, sql, params)
        )
        {
            List<StockData> stockDatas = new ArrayList<>(crs.size());

            while(crs.next())
            {
                stockDatas.add(
                        new StockData(
                                crs.getInt("id"),
                                crs.getString("data"),
                                crs.getTimestamp("last_updated")
                        )
                );
            }

            return stockDatas;
        }
        catch (SQLException e)
        {
            logger.error("Exception occurred while finding stock data(s) in database\n", e);
        }
        return Collections.emptyList();
    }

    static Optional<StockData> Create(String data, Timestamp lastUpdated)
    {
        logger.info("Creating new stock data...");

        try(Connection conn = StockDatabase.getConnection())
        {
            int id = SqlCmd.executeInsert(conn, "INSERT INTO stocks_data(data, last_updated) VALUES(?, ?)", data, lastUpdated);
            return Optional.of(new StockData(id, data, lastUpdated));
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
            SqlCmd.executeUpdate(conn, "UPDATE stocks_data SET data = ?, last_updated = ? WHERE id = ?", data, lastUpdated, id);
        }
    }

    @Override
    public void commit(Connection connection) throws SQLException
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete() throws SQLException
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(Connection conn) throws SQLException
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
