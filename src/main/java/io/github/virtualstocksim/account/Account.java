package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.DatabaseItem;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.stock.StockDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;


public class Account extends DatabaseItem {
    private static final Logger logger = LoggerFactory.getLogger(Account.class);

    private String uname;
    private String pword;
    private String email;
    private String bio;
    private String uuid;
    private byte[] passwordHash;
    private byte[] passwordSalt;
    private int leaderboardRank;
    //private StocksFollowed stocksFollowed;
   // private TransactionHistory transactionHistory;
    /**
     * IMPORTANT: These are being changed to strings for the time being, as we are still testing. They will
     * eventually be objects again
     */
    private String stocksFollowed;
    private String transactionHistory;
    private AccountType accountType;
    private String profilePicture;
    private final Timestamp timestamp;
    private BigDecimal accountBal;

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
     * @param timestamp time the account was created (java.time)
     */
    public Account(int id, String uuid, AccountType accountType, String email, String username, byte[] passwordHash, byte[] passwordSalt,
                   String stocksFollowed, String transactionHistory,
                   int leaderboardRank, String bio, String profilePicture, Timestamp timestamp) {
        super(id);
        this.uuid = uuid;
        this.accountType = accountType;
        this.uname = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.stocksFollowed = stocksFollowed;
        this.transactionHistory = transactionHistory;
        this.leaderboardRank = leaderboardRank;
        this.bio = bio;
        this.profilePicture = profilePicture;
        this.timestamp = timestamp;
        this.accountBal = new BigDecimal(500.50); //Brett is adding this for now, will change constructor later
    }                          //Just for laying down ground work of AccountController.trade


    public String getUname(){
        return this.uname;
    }

    public String getPword(){
        return this.pword;
    }

    public String getEmail(){
        return this.email;
    }

    public String getUUID() {
        return this.uuid;
    }

    public AccountType getAccountType() {
        return this.accountType;
    }

    public byte[] getPasswordHash() {
        return this.passwordHash;
    }

    public byte[] getPasswordSalt() {
        return this.passwordSalt;
    }

    public BigDecimal getAccountBal(){return this.accountBal;}

    /**
     * THESE SHOULD EVENTUALLY BE CHANGED BACK TO THEIR RESPECTIVE OBJECTS AFTER TESTING
     * @return stocks user is following in a StocksFollowed object
     */
    public String getStocksFollowed(){return this.stocksFollowed;}

    /**
     * THESE SHOULD EVENTUALLY BE CHANGED BACK TO THEIR RESPECTIVE OBJECTS AFTER TESTING
     * @return User's transactions in a transactionHistory object
     */
    public String getTransactionHistory() {return this.transactionHistory; }

    public int getLeaderboardRank() {return this.leaderboardRank;}

    public String getBio() {
        return this.bio;
    }

    public String getProfilePicture(){
        return this.profilePicture;
    }

    public Timestamp getCreationDate(){
        return this.timestamp;
    }

    public void setUname(String uname){
        this.uname = uname;
    }

    public void setPword(String pword){
        this.pword = pword;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setUuid(String UUID){
        this.uuid = UUID;
    }
    public void setAccountBal(BigDecimal newBalance){ this.accountBal = newBalance;}

    public void setPasswordHash(byte[] passwordHash){
        this.passwordHash = passwordHash;
    }

    public void setPasswordSalt(byte[] passwordSalt){this.passwordSalt = passwordSalt;}

    public void setLeaderboardRank(int leaderboardRank){
        this.leaderboardRank = leaderboardRank;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }



    // Static methods to search database based on given parameter
    public static Optional<Account> Find(int id){return Find("id", id);}
    public static Optional<Account> Find(String username){return Find("username", username);}
    public static Optional<Account> Find(String key, Object value)
    {
        List<Account> accounts = FindCustom(String.format("SELECT id, uuid, type, username, email, password_hash, " +
                "password_salt, followed_stocks, transaction_history, leaderboard_rank, bio, profile_picture, creation_date " +
                "FROM accounts WHERE %s = ?", key), value);

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
    public static List<Account> FindCustom(String sql, Object... params) {
        /**
         * IMPORTANT: This is not finished and needs constructors from transactionHistory and stocks followed to pull
         * a string from the DB and parse it into the respective objects. Right now there are hardcoded values placed in
         * for testing. - Dan
         */
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
                                columns.containsKey("uuid")                 ? crs.getString("uuid")                 : null,
                                columns.containsKey("type")                 ? AccountType.valueOf(crs.getString("type"))                       : null,
                                columns.containsKey("email")                ? crs.getString("email")                : null,
                                columns.containsKey("username")             ? crs.getString("username")             : null,
                                columns.containsKey("password_hash")        ? crs.getBytes("password_hash")         : null,
                                columns.containsKey("password_salt")        ? crs.getBytes("password_salt")         : null,
                                columns.containsKey("followed_stocks")      ? crs.getString("followed_stocks")      : null,
                                columns.containsKey("transaction_history")  ? transactionHistory                                : null,
                                columns.containsKey("leaderboard_rank")     ? crs.getInt("leaderboard_rank")        : -1,
                                columns.containsKey("bio")                  ? crs.getString("bio")                  : null,
                                columns.containsKey("profile_picture")      ? crs.getString("profile_picture")      : null,
                                columns.containsKey("creation_date")        ? crs.getTimestamp("creation_date")     : null
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
        String blank_string ="";
        int defaultLeaderboardRank = -1;
        logger.info("Attempting to create a new account in account database...");

        try(Connection conn = AccountDatabase.getConnection())
        {
            int id = SQL.executeInsert(conn,
                    "INSERT INTO accounts (uuid, type, email, username, password_hash, password_salt, " +
                            " followed_stocks, transaction_history, leaderboard_rank, bio, profile_picture, creation_date) " +

                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",uuid, accountType.getText(), email, username, hash, salt, blank_string,
                    blank_string, defaultLeaderboardRank, blank_string, blank_string, timestamp);

            logger.info("Account with new id " + id + " successfully created!");
            return Optional.of(new Account(id, uuid, accountType, email, username, hash, salt, "",
                    "", defaultLeaderboardRank, blank_string, blank_string, timestamp));

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
        columns.put("type", accountType);
        columns.put("email", email);
        columns.put("username", uname);
        columns.put("password_hash", passwordHash);
        columns.put("password_salt", passwordSalt);
        columns.put("followed_stocks", stocksFollowed);
        columns.put("transaction_history", transactionHistory);//change this to deserialized params from object
        columns.put("leaderboard_rank", leaderboardRank);
        columns.put("bio", bio);
        columns.put("profile_picture", profilePicture);

        // Check each column name and add it to the update list if its been updated
        for(Map.Entry<String, Object> c : columns.entrySet())
        {
            if(c.getValue() != null)
            {
                updated.add(c.getKey() + " = ?");
                params.add(c.getValue());
            }
        }

        if(updated.isEmpty())
        {
            logger.warn(String.format("Abandoning update for Account ID %d; Nothing to update", id));
        }
        else
        {
            params.add(id);
            SQL.executeUpdate(connection, String.format("UPDATE accounts SET %s WHERE id = ?", String.join(", ", updated)), params.toArray());
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
        SQL.executeUpdate(conn, "DELETE FROM accounts WHERE id = ?", id);
    }
}
