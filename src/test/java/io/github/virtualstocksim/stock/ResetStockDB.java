package io.github.virtualstocksim.stock;

import io.github.virtualstocksim.database.SQL;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ResetStockDB
{
    private static final Logger logger = LoggerFactory.getLogger(ResetStockDB.class);
    private static final List<String> resetCmds = new LinkedList<>();

    static
    {
        try
        {
            List<String> cmdFiles = new LinkedList<>();
            cmdFiles.add("/stockSqlCmds/fill_stock_data_table.txt");
            cmdFiles.add("/stockSqlCmds/fill_stock_table.txt");

            for(String file : cmdFiles)
            {
                resetCmds.add(IOUtils.toString(ResetStockDB.class.getResourceAsStream(file), StandardCharsets.UTF_8));
            }
        }
        catch (IOException e)
        {
            logger.error("", e);
        }

    }

    public static void reset()
    {
        try(Connection conn = StockDatabase.getConnection())
        {
            conn.setAutoCommit(false);

            SQL.executeUpdate(conn, "DELETE FROM stock");
            SQL.executeUpdate(conn, "DELETE FROM stock_data");

            for(String cmd : resetCmds)
            {
                SQL.executeUpdate(conn, cmd);
            }

            conn.commit();
        }
        catch (SQLException e)
        {
            logger.info("", e);
        }
    }

    private static boolean tableExists(Connection conn, String table) throws SQLException
    {
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs = metaData.getTables(null, "APP", table.toUpperCase(), null);

        return rs.next() && rs.getString(3).equals(table.toUpperCase());
    }
}
