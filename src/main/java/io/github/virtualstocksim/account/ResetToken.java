package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.DatabaseItem;
import io.github.virtualstocksim.database.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class ResetToken extends DatabaseItem
{
    private static final Logger logger = LoggerFactory.getLogger(ResetToken.class);

    private int accountId;
    private String token;
    private Timestamp expiration;

    protected ResetToken(int id, int accountId, byte[] token, Timestamp expiration)
    {
        this(id, accountId, Base64.getUrlEncoder().withoutPadding().encodeToString(token), expiration);
    }

    protected ResetToken(int id, int accountId, String token, Timestamp expiration)
    {
        super(id);
        this.accountId = accountId;
        this.token = token;
        this.expiration = expiration;
    }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getToken() { return token; }

    /**
     * @param token Reset token as a Base64 url-safe encoded string
     */
    public void setToken(String token) { this.token = token; }
    public void setToken(byte[] token)
    {
        setToken(Base64.getUrlEncoder().withoutPadding().encodeToString(token));
    }

    public Timestamp getExpiration() { return expiration; }
    public void setExpiration(Timestamp expiration) { this.expiration = expiration; }

    public static Optional<ResetToken> Find(int id)
    {
        return Find("id", id);
    }

    public static Optional<ResetToken> FindByAccountId(int id)
    {
        return Find("account_id", id);
    }

    /**
     * @param token Reset token as a Base64 url-safe encoded string
     */
    public static Optional<ResetToken> Find(String token)
    {
        return Find("token", token);
    }

    /**
     * Find an existing reset token in the database
     * @param key Column to use in the `where` clause of the search
     * @param value Value to search for `where` clause of the search
     * @return ResetToken instance if found, otherwise empty if not
     */
    public static Optional<ResetToken> Find(String key, Object value)
    {
        List<ResetToken> resetTokens = FindCustom("SELECT id, account_id, token, expiration FROM reset_token WHERE " + key + " = ?", value);
        return resetTokens.isEmpty() ? Optional.empty() : Optional.of(resetTokens.get(0));
    }

    /**
     * Search for one or more reset tokens with a custom SQL command.
     * Any empty fields are set to null or -1
     * Query MUST include a returned ID
     * @param sql SQL command
     * @param params SQL command parameters
     * @return List of ResetToken instances
     */
    public static List<ResetToken> FindCustom(String sql, Object... params)
    {
        logger.info("Searching for reset token(s)...");
        try(Connection conn = AccountDatabase.getConnection();
            CachedRowSet crs = SQL.executeQuery(conn, sql, params)
        )
        {
            List<ResetToken> resetTokens = new ArrayList<>(crs.size());

            Map<String, Void> columns = SQL.GetColumnNameMap(crs.getMetaData());

            // Make sure that the query returned an ID
            if(!columns.containsKey("id"))
            {
                throw new SQLException("Query must return ID");
            }

            // Iterate through all returned reset tokens and create a stock instance for each
            while(crs.next())
            {
                resetTokens.add(
                        new ResetToken(
                                crs.getInt("id"),
                                columns.containsKey("account_id")   ? crs.getInt("account_id")         : -1,
                                columns.containsKey("token")        ? crs.getString("token")           : null,
                                columns.containsKey("expiration")   ? crs.getTimestamp("expiration")   : null
                        )
                );
            }

            return resetTokens;
        }
        catch (SQLException e)
        {
            logger.error("Exception occurred while finding reset token(s) in database\n", e);
        }

        return Collections.emptyList();
    }

    /**
     * Create a new reset token
     * @param accountId ID of the account this reset token is for
     * @param token Reset token
     * @param expiration When this reset token should expire
     * @return ResetToken instance of the newly created reset token
     */
    public static Optional<ResetToken> Create(int accountId, byte[] token, Timestamp expiration)
    {
        return Create(accountId, Base64.getUrlEncoder().withoutPadding().encodeToString(token), expiration);
    }

    /**
     * Create a new reset token
     * @param accountId ID of the account this reset token is for
     * @param token Reset token as a Base64 url-safe encoded string
     * @param expiration When this reset token should expire
     * @return ResetToken instance of the newly created reset token
     */
    public static Optional<ResetToken> Create(int accountId, String token, Timestamp expiration)
    {
        try(Connection conn = AccountDatabase.getConnection())
        {
            logger.info("Creating new reset token...");

            int id = SQL.executeInsert(conn, "INSERT INTO reset_token(account_id, token, expiration) VALUES(?, ?, ?)", accountId, token, expiration);

            return Optional.of(new ResetToken(id, accountId, token, expiration));
        }
        catch (SQLException e)
        {
            logger.error("ResetToken creation failed\n", e);
            return Optional.empty();
        }
    }

    @Override
    public void update() throws SQLException
    {
        try(Connection conn = AccountDatabase.getConnection())
        {
            update(conn);
        }
    }

    /**
     * Commit ResetToken to database
     * @param conn Connection to Account database
     * @throws SQLException
     */
    @Override
    public void update(Connection conn) throws SQLException
    {
        logger.info("Committing reset token changes to database for ResetToken with ID " + id);

        List<String> updated = new LinkedList<>();
        List<Object> params = new LinkedList<>();

        // Map of column names and values
        Map<String, Object> columns = new HashMap<>();
        if(accountId != -1)                                 columns.put("account_id", accountId);
        if(token != null && !token.trim().isEmpty())        columns.put("token", token);
        if(expiration != null)                              columns.put("expiration", expiration);

        for(Map.Entry<String, Object> c : columns.entrySet())
        {
            updated.add(c.getKey() + " = ?");
            params.add(c.getValue());
        }

        if(updated.isEmpty())
        {
            logger.warn("Abandoning update for ResetToken with ID " + id + "; Nothing to update");
        }
        else
        {
            params.add(id);
            SQL.executeUpdate(conn, "UPDATE reset_token SET " + String.join(", ", updated + " WHERE id = ?"), params.toArray());
        }
    }

    @Override
    public void delete() throws SQLException
    {
        try(Connection conn = AccountDatabase.getConnection())
        {
            delete(conn);
        }
    }

    @Override
    public void delete(Connection conn) throws SQLException
    {
        logger.info("Removing ResetToken with ID " + id + " from database");
        SQL.executeUpdate(conn, "DELETE FROM stock WHERE id = ?", id);
    }
}
