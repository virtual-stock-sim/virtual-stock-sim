package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.DatabaseException;
import io.github.virtualstocksim.database.DatabaseItem;
import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.following.Follow;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    private StocksFollowed stocksFollowed;
    private TransactionHistory transactionHistory;
    private AccountType type;
    private String profilePicture;
    private final String timestamp;


    /**
     *
     * @param id account ID for referencing in database
     * @param uuid  Unique identifier for each account created
     * @param type Enum of accountType; i.e. "admin" or "user"
     * @param username User's username
     * @param passwordHash password hash for encryption
     * @param passwordSalt password salt used to hash a password
     * @param stocksFollowed Linked list of Stocks that user is following
     * @param transactionHistory TransactionHistory object, which is a List of transaction objects
     * @param leaderboardRank  User's rank, among other users, of total profit gained. Initially set to -1
     * @param bio User's bio
     * @param profilePicture String Path to profile picture locally on server
     * @param timestamp time the account was created (java.time)
     */
    public Account(int id, String uuid, AccountType type, String email, String username, byte[] passwordHash, byte[] passwordSalt,
                   StocksFollowed stocksFollowed, TransactionHistory transactionHistory,
                   int leaderboardRank, String bio, String profilePicture, String timestamp) {
        super(id);
        this.uuid = uuid;
        this.type = type;
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

    /*public Optional<Account> findAccount(int id){
        try
        {
            logger.info("Searching for account...");
            ResultSet rs = accountDatabase.executeQuery(String.format("SELECT uuid, type, email, username, password_hash, " +
                    "password_salt, followed_stocks, transaction_history, leaderboard_rank, bio, profile_picture, timestamp" +
                   "FROM accounts WHERE %s = ?",id),id);

            // Return empty if nothing was found
            if(!rs.next()) return Optional.empty();

            // else return the account found
            return Optional.of(
                    new Account(
                            id,
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
                            rs.getString("timestamp")
                    )
            );
        }
        catch (DatabaseException e)
        {
            logger.error(String.format("Account with search parameter %s not found\n", id), e);
        }
        catch(SQLException e)
        {
            logger.error("Error while parsing result from account database\n", e);
        }
        return Optional.empty();
    }
    }*/

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
        return this.type;
    }

    public byte[] getPasswordHash() {
        return this.passwordHash;
    }

    public byte[] getPasswordSalt() {
        return this.passwordSalt;
    }

    public StocksFollowed getStocksFollowed(){return this.stocksFollowed;}

    public TransactionHistory getTransactionHistory() {return this.transactionHistory; }

    public int getLeaderboardRank() {return this.leaderboardRank;}

    public String getBio() {
        return this.bio;
    }

    public String getProfilePicture(){
        return this.profilePicture;
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

    public void updateProfilePicture(int accountID, String newPicturePath) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Account createAccountInDB(String username, String email, String password, AccountType type)
    {
        Encryption encrypt = new Encryption();
        Date date = new Date();
        byte[] salt, hash;
        String timestamp = date.toString();
        String uuid = UUID.randomUUID().toString();
        salt = encrypt.getNextSalt();
        hash = encrypt.hash(password.toCharArray(), salt);
        String blank_string ="";

        int id = accountDatabase.executeInsert("INSERT INTO account (uuid, type, email, username, password_hash, password_salt," +
                " followed_stocks, transaction_history, leaderboard_rank, bio, profile_picture, timestamp)" +
                " VALUES(%s, %s, %s, %s, %s, %s, %s, -1, %s, %s, %s",uuid, type, email, username, hash, salt, blank_string, blank_string, -1,
                blank_string, blank_string, timestamp);

            // return newly created account
        return new Account(id, uuid, type, email, username, hash, salt, new StocksFollowed(new LinkedList<Follow>()),
              new TransactionHistory(new LinkedList<Transaction>()), -1, blank_string, blank_string, timestamp);

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
