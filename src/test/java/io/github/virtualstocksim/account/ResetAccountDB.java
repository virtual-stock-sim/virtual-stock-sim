package io.github.virtualstocksim.account;

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

public class ResetAccountDB
{
    private static final Logger logger = LoggerFactory.getLogger(ResetAccountDB.class);
    private static final List<String> resetCmds = new LinkedList<>();

    static
    {
        try
        {
            List<String> cmdFiles = new LinkedList<>();
            cmdFiles.add("/accountSqlCmds/create_account_table.txt");
            cmdFiles.add("/accountSqlCmds/create_reset_token_table.txt");
            //cmdFiles.add("/accountSqlCmds/fill_account_table.txt");

            for(String file : cmdFiles)
            {
                resetCmds.add(IOUtils.toString(ResetAccountDB.class.getResourceAsStream(file), StandardCharsets.UTF_8));
            }
        }
        catch (IOException e)
        {
            logger.error("", e);
        }

    }

    public static void reset()
    {
        try(Connection conn = AccountDatabase.getConnection())
        {
            conn.setAutoCommit(false);

            if(tableExists(conn, "reset_token"))
            {
                SQL.executeUpdate(conn, "DROP TABLE reset_token");
            }

            if(tableExists(conn, "account"))
            {
                SQL.executeUpdate(conn, "DROP TABLE account");
            }

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
