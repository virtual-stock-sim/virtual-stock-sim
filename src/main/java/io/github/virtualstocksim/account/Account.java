package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.DatabaseItem;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;


public class Account extends DatabaseItem {
    private static final Logger logger = LoggerFactory.getLogger(Account.class);
    private static final String EMPTY_STRING = "";
    private static final BigDecimal EMPTY_BD = new BigDecimal("0.0");
    private static final String PROFILE_PICTURE_DIR ="./war/userdata/ProfilePictures/";

    private final String uuid;
    private AccountType type;
    private String username;
    private String email;
    private byte[] passwordHash;
    private byte[] passwordSalt;
    private String followedStocks;
    private String investedStocks;
    private String transactionHistory;
    private BigDecimal walletBalance;
    private int leaderboardRank;
    private String bio;
    private String profilePicture;
    private final Timestamp creationDate;


    /**
     *
     * @param id account ID for referencing in database
     * @param uuid  Unique identifier for each account created
     * @param accountType type of account i.e. "admin" or "user"
     * @param username User's username
     * @param passwordHash password hash for encryption
     * @param passwordSalt password salt used to hash a password
     * @param stocksFollowed Linked list of Stocks that user is following
     * @param transactionHistory TransactionHistory object, which is a List of transaction objects
     * @param leaderboardRank  User's rank, among other users, of total profit gained. Initially set to -1
     * @param bio User's bio (string)
     * @param profilePicture String Path to profile picture locally on server
     * @param creationDate time the account was created (java.time)
     */
    public Account(
            int id,
            String uuid,
            AccountType accountType,
            String email,
            String username,
            byte[] passwordHash,
            byte[] passwordSalt,
            String stocksFollowed,
            String investedStocks,
            String transactionHistory,
            BigDecimal walletBalance,
            int leaderboardRank,
            String bio,
            String profilePicture,
            Timestamp creationDate
                  ) {
        super(id);
        this.uuid = uuid;
        this.type = accountType;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.followedStocks = stocksFollowed;
        this.investedStocks = investedStocks;
        this.transactionHistory = transactionHistory;
        this.walletBalance = walletBalance;
        this.leaderboardRank = leaderboardRank;
        this.bio = bio;
        this.profilePicture = profilePicture;
        this.creationDate = creationDate;

    }

    public String getUUID()
    {
        return uuid;
    }

    public AccountType getType()
    {
        return type;
    }

    public void setType(AccountType type)
    {
        this.type = type;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public byte[] getPasswordHash()
    {
        return passwordHash;
    }

    public void setPasswordHash(byte[] passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    public byte[] getPasswordSalt()
    {
        return passwordSalt;
    }

    public void setPasswordSalt(byte[] passwordSalt)
    {
        this.passwordSalt = passwordSalt;
    }

    public String getFollowedStocks()
    {
        return followedStocks;
    }

    public void setFollowedStocks(String followedStocks)
    {
        this.followedStocks = followedStocks;
    }

    public String getInvestedStocks()
    {
        return investedStocks;
    }

    public void setInvestedStocks(String investedStocks)
    {
        this.investedStocks = investedStocks;
    }

    public String getTransactionHistory()
    {
        return transactionHistory;
    }

    public void setTransactionHistory(String transactionHistory)
    {
        this.transactionHistory = transactionHistory;
    }

    public BigDecimal getWalletBalance()
    {
        return walletBalance;
    }

    public void setWalletBalance(BigDecimal walletBalance)
    {
        this.walletBalance = walletBalance;
    }

    public int getLeaderboardRank()
    {
        return leaderboardRank;
    }

    public void setLeaderboardRank(int leaderboardRank)
    {
        this.leaderboardRank = leaderboardRank;
    }

    public String getBio()
    {
        return bio;
    }

    public void setBio(String bio)
    {
        this.bio = bio;
    }

    public String getProfilePicture()
    {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture)
    {
        this.profilePicture = profilePicture;
    }

    public Timestamp getCreationDate()
    {
        return creationDate;
    }

    public String getProfilePictureDirectory()
    {
        return PROFILE_PICTURE_DIR;
    }

    public String getProfilePictureWithDir(){
        return PROFILE_PICTURE_DIR + profilePicture;
    }

    // Static methods to search database based on given parameter
    public static Optional<Account> Find(int id){return Find("id", id);}
    public static Optional<Account> Find(String username){return Find("username", username);}
    public static Optional<Account> Find(String key, Object value)
    {
        List<Account> accounts = FindCustom(
                String.format(
                        "SELECT " +
                                "id, " +
                                "uuid, " +
                                "type, " +
                                "username, " +
                                "email, " +
                                "password_hash, " +
                                "password_salt, " +
                                "followed_stocks, " +
                                "invested_stocks, " +
                                "transaction_history, " +
                                "leaderboard_rank, " +
                                "wallet_balance, " +
                                "bio, " +
                                "profile_picture, " +
                                "creation_date " +
                                "FROM account WHERE %s = ?",
                        key),
                value);

        if (accounts.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(accounts.get(0));
        }
    }


    /**
     * Search for one or more accounts with a custom SQL command
     * @param sql SQL command
     * @param params SQL command parameters
     * @return List of Account instances
     */
    public static List<Account> FindCustom(String sql, Object... params)
    {
        logger.info("Searching for account(s)...");
        try(Connection conn = AccountDatabase.getConnection();
            CachedRowSet crs = SQL.executeQuery(conn, sql, params);
        )
        {
            List<Account> accounts = new ArrayList<>(crs.size());

            ResultSetMetaData rsmd = crs.getMetaData();

            // HashMap of column names returned in result
            HashMap<String, Void> columns = new HashMap<>();
            for(int i = 1; i <= rsmd.getColumnCount(); ++i)
            {
                columns.put(rsmd.getColumnName(i).toLowerCase(), null);
            }

            // Make sure query returned an ID
            if(!columns.containsKey("id"))
            {
                throw new SQLException("Query must return ID");
            }

            while(crs.next())
            {
                // Attempt to read clob
                String transactionHistory = null;
                if(columns.containsKey("transaction_history"))
                {
                    Clob clob = crs.getClob("transaction_history");
                    if(clob.length() > 0)
                    {
                        transactionHistory = clob.getSubString(1, (int) clob.length());
                    }
                }

                accounts.add(
                        new Account(
                                crs.getInt("id"),
                                columns.containsKey("uuid")                 ? crs.getString("uuid")                         : null,
                                columns.containsKey("type")                 ? AccountType.valueOf(crs.getString("type"))    : null,
                                columns.containsKey("email")                ? crs.getString("email")                        : null,
                                columns.containsKey("username")             ? crs.getString("username")                     : null,
                                columns.containsKey("password_hash")        ? crs.getBytes("password_hash")                 : null,
                                columns.containsKey("password_salt")        ? crs.getBytes("password_salt")                 : null,
                                columns.containsKey("followed_stocks")      ? crs.getString("followed_stocks")              : null,
                                columns.containsKey("invested_stocks")      ? crs.getString("invested_stocks")              : null,
                                columns.containsKey("transaction_history")  ? crs.getString("transaction_history")          : null,
                                columns.containsKey("wallet_balance")       ? crs.getBigDecimal("wallet_balance")           : null,
                                columns.containsKey("leaderboard_rank")     ? crs.getInt("leaderboard_rank")                : -1,
                                columns.containsKey("bio")                  ? crs.getString("bio")                          : null,
                                columns.containsKey("profile_picture")      ? crs.getString("profile_picture")              : null,
                                columns.containsKey("creation_date")        ? crs.getTimestamp("creation_date")             : null
                        )
                            );
            }
            return accounts;
        }
        catch (SQLException e)
        {
            logger.error("Exception occurred while finding account(s) in database\n", e);
        }
        return Collections.emptyList();
    }

    /**
     * @param username username of user
     * @param email email of user
     * @param password user's password to be encrypted
     * @param accountType AccountType (Admin or User)
     * @return Either a newly created account, or empty if creation fails.
     */
    public static Optional<Account> Create(String username, String email, String password, AccountType accountType)
    {
        byte[] salt, hash;
        Timestamp timestamp = SQL.GetTimeStamp();
        String uuid = UUID.randomUUID().toString();
        salt = Encryption.getNextSalt();
        hash = Encryption.hash(password.toCharArray(), salt);
        int defaultLeaderboardRank = -1;
        logger.info("Attempting to create a new account in account database...");

        try(Connection conn = AccountDatabase.getConnection())
        {
            int id = SQL.executeInsert(conn,
                                       "INSERT INTO account (" +
                                               "uuid, " +
                                               "type, " +
                                               "email, " +
                                               "username, " +
                                               "password_hash, " +
                                               "password_salt, " +
                                               "followed_stocks, " +
                                               "invested_stocks, " +
                                               "transaction_history, " +
                                               "wallet_balance, " +
                                               "leaderboard_rank, " +
                                               "bio, " +
                                               "profile_picture, " +
                                               "creation_date" +
                                               ") " +
                                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",
                                       uuid,
                                       accountType.getText(),
                                       email,
                                       username,
                                       hash,
                                       salt,
                                       EMPTY_STRING,
                                       EMPTY_STRING,
                                       EMPTY_STRING,
                                       EMPTY_BD,
                                       defaultLeaderboardRank,
                                       EMPTY_STRING,
                                       EMPTY_STRING,
                                       timestamp
                                      );

            logger.info("Account with new id " + id + " successfully created!");
            return Optional.of(
                    new Account
                            (
                                    id,
                                    uuid,
                                    accountType,
                                    email,
                                    username,
                                    hash,
                                    salt,
                                    EMPTY_STRING,
                                    EMPTY_STRING,
                                    EMPTY_STRING,
                                    EMPTY_BD,
                                    defaultLeaderboardRank,
                                    EMPTY_STRING,
                                    EMPTY_STRING,
                                    timestamp
                            )
                              );

        } catch (SQLException e){
            logger.info("Account creation failed\n", e);
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

    @Override
    public void update(Connection connection) throws SQLException
    {
        logger.info(String.format("Committing stock changes to database for account ID %d", id));

        List<String> updated = new LinkedList<>();
        List<Object> params = new LinkedList<>();

        // Map of column names and values
        Map<String, Object> columns = new HashMap<>();
        if(type!=null)                                                          columns.put("type", type.getText());
        if(email!=null && !email.trim().isEmpty())                              columns.put("email", email);
        if(username!=null && !username.trim().isEmpty())                        columns.put("username", username);
        if(passwordHash!=null)                                                  columns.put("password_hash", passwordHash);
        if(passwordSalt!=null)                                                  columns.put("password_salt", passwordSalt);
        if(followedStocks!=null && !followedStocks.trim().isEmpty())            columns.put("followed_stocks", followedStocks);
        if(investedStocks!=null && !investedStocks.trim().isEmpty())            columns.put("invested_stocks", investedStocks);
        if(transactionHistory!=null && !transactionHistory.trim().isEmpty())    columns.put("transaction_history", transactionHistory);
        if(walletBalance!=null)                                                 columns.put("wallet_balance", walletBalance);
        if(leaderboardRank > 0)                                                 columns.put("leaderboard_rank", leaderboardRank);
        if(bio!=null && !bio.trim().isEmpty())                                  columns.put("bio", bio);
        if(profilePicture!=null && !profilePicture.trim().isEmpty())            columns.put("profile_picture", profilePicture);

        // Check each column name and add it to the update list if its been updated
        for(Map.Entry<String, Object> c : columns.entrySet())
        {
                updated.add(c.getKey() + " = ?");
                params.add(c.getValue());
        }

        if(updated.isEmpty())
        {
            logger.warn(String.format("Abandoning update for Account ID %d; Nothing to update", id));
        }
        else
        {
            params.add(id);
            SQL.executeUpdate(connection, String.format("UPDATE account SET %s WHERE id = ?", String.join(", ", updated)), params.toArray());
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
        // deleting an account from database
        logger.info(String.format("Removing Account with ID %d from database", id));
        SQL.executeUpdate(conn, "DELETE FROM account WHERE id = ?", id);
    }

    public static long ProfilePictureMaxFileSize() { return 2097152; }
    public static int ProfilePictureMaxWidth() { return 328; }
    public static int ProfilePictureMaxHeight() { return 328; }
}
