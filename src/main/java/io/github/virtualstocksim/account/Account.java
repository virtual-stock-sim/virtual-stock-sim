package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.DatabaseItem;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionHistory;

import java.util.LinkedList;


public class Account extends DatabaseItem {
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
     */
    public Account(int id, String uuid, AccountType type, String email, String username, byte[] passwordHash, byte[] passwordSalt,
                   StocksFollowed stocksFollowed, TransactionHistory transactionHistory,
                   int leaderboardRank, String bio, String profilePicture) {
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

    public void updateProfilePicture(int accountID)
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void commit()
    {

    }

}
