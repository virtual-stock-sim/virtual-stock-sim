package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.DatabaseException;
import io.github.virtualstocksim.database.DatabaseItem;
import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.following.Follow;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionHistory;
import io.github.virtualstocksim.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;


public class Account extends DatabaseItem {
    private static final Logger logger = LoggerFactory.getLogger(Account.class);
    private static AccountDatabase accountDatabase = AccountDatabase.Instance();

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
    private String accountType;
    private String profilePicture;
    private final Timestamp timestamp;


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
    public Account(int id, String uuid, String accountType, String email, String username, byte[] passwordHash, byte[] passwordSalt,
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

    }


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

    public String getAccountType() {
        return this.accountType;
    }

    public byte[] getPasswordHash() {
        return this.passwordHash;
    }

    public byte[] getPasswordSalt() {
        return this.passwordSalt;
    }

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

    /**
     *
     * @param accountID Account id to update
     * @param newPicturePath Updated picture
     */
    public void updateProfilePicture(int accountID, String newPicturePath) {
        throw new UnsupportedOperationException("Not implemented yet");
    }



    /**
     * @param searchCol Column of DB to use in the WHERE portion of the SQL statement
     * @param colValue Value of column to search by
     * @return returned account, if any. (Could be empty if account does not exist)
     */
    public static Optional<Account> Find(String searchCol, Object colValue) {
        /**
         * IMPORTANT: This is not finished and needs constructors from transactionHistory and stocks followed to pull
         * a string from the DB and parse it into the respective objects. Right now there are hardcoded values placed in
         * for testing. - Dan
         */
        try {
            logger.info("Searching for account...");
            ResultSet rs = accountDatabase.executeQuery(String.format("SELECT id, uuid, type, username, email, password_hash, " +
                    "password_salt, followed_stocks, transaction_history, leaderboard_rank, bio, profile_picture, creation_date " +
                    "FROM accounts WHERE %s = ?", searchCol), colValue);

            // Return empty if nothing was found
            if(!rs.next()) return Optional.empty();

            // else return the account found
            return Optional.of(
                    new Account(
                            rs.getInt("id"),
                            rs.getString("uuid"),
                            rs.getString("type"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getBytes("password_hash"),
                            rs.getBytes("password_salt"),
                            rs.getString("followed_stocks"),
                            rs.getString("transaction_history"),
                            rs.getInt("leaderboard_rank"),
                            rs.getString("bio"),
                            rs.getString("profile_picture"),
                            rs.getTimestamp("creation_date")
                    )
            );
        }
        catch (DatabaseException e)
        {
            logger.error(String.format("Account with search parameter %s not found\n", colValue), e);
        }
        catch(SQLException e)
        {
            logger.error("Error while parsing result from account database\n", e);
        }
        return Optional.empty();
    }




    /**
     * @param username username of user
     * @param email email of user
     * @param password user's password to be encrypted
     * @param accountType AccountType (Admin or User)
     * @return Either a newly created account, or empty if creation fails.
     */
    public static Optional<Account> Create(String username, String email, String password, String accountType)
    {
        Encryption encrypt = new Encryption();
        byte[] salt, hash;
        Timestamp timestamp = Util.GetTimeStamp();
        String uuid = UUID.randomUUID().toString();
        salt = encrypt.getNextSalt();
        hash = encrypt.hash(password.toCharArray(), salt);
        String blank_string ="";
        int defaultLeaderboardRank = -1;
        logger.info("Attempting to create a new account in account database...");

        try {
            int id = accountDatabase.executeInsert(
                    "INSERT INTO accounts (uuid, type, username, email, password_hash, password_salt, " +
                            " followed_stocks, transaction_history, leaderboard_rank, bio, profile_picture, creation_date) " +

                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",uuid, accountType, username, email, hash, salt, blank_string,
                    blank_string, defaultLeaderboardRank, blank_string, blank_string, timestamp);

            logger.info("Account with new id " + id + " successfully created!");
            return Optional.of(new Account(id, uuid, accountType, username, email, hash, salt, "",
                    "", defaultLeaderboardRank, blank_string, blank_string, timestamp));

        } catch (DatabaseException e){
            logger.info("Account creation failed");
            return Optional.empty();
        }

            // return newly created account
        // generate password hash and salt
        // generate the timestamp
        // generate the UUID
        // place these into new account in database for empty string for blank fields
        // -1 for leaderboard rank
        /*
        Create(type, email, username, password):
          uuid = genUUID()
          salt = getSalt()
          hash = genHash(password, salt)
          // database insert method returns generated primary key id of what was just inserted
          id = insertIntoDB(uuid, type, email, username, salt, hash, empty string for stocks followed, empty list
          for transaction history, -1 for leaderboard rank, empty string for bio, empty string for profile picture)
          return new Account(params)

          test fields returned from db against fields placed into database and make sure they match
         */
    }

    public void commit()
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
