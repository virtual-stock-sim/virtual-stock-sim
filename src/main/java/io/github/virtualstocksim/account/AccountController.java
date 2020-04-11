package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.scraper.Scraper;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class AccountController {
    // account instance
    private Account acc;
    private static final Logger logger = LoggerFactory.getLogger(Account.class);

    private io.github.virtualstocksim.scraper.Scraper scraper;

    public void setModel (Account acc){
        this.acc=acc;
    }

    /**
     *
     * @param username username provided
     * @param password  password provided - will be hashed and checked against that stored in database
     * @return Account found with specified username and password parameters, if any
     */
    public static Optional<Account> login(String username, String password)
    {
        logger.info("Logging user " + username + " in...");

        try(Connection conn = AccountDatabase.getConnection();
            CachedRowSet passwordCheckRS = SQL.executeQuery(conn, String.format("SELECT password_hash, password_salt " +
                    "from accounts " +
                    " where username = ? "), username);)
        {
            if(!passwordCheckRS.next()){
                return Optional.empty();
            }
            boolean isValid = Encryption.validateInput(password.toCharArray(), passwordCheckRS.getBytes("password_salt"), passwordCheckRS.getBytes("password_hash"));

            // check if credentials are valid
            if (isValid) {
                return Account.Find(username);
            }else{
                return Optional.empty();
            }

        } catch (SQLException e){
            logger.error("Error while parsing result from accounts database\n", e);

        }
        logger.info("Couldn't find account with username "+username);
        return Optional.empty();
    }



    /**
     *
     * @param accountID Account id to update
     * @param newPicturePath Updated picture path
     */
    public void updateProfilePicture(int accountID, String newPicturePath) {
        acc = Account.Find(accountID).get();
        acc.setProfilePicture(newPicturePath);

        try{
            acc.update();
            logger.info("ProfilePicture updated successfully!");
        } catch(SQLException e){
            logger.error("Error: " + e.toString());
        }

        // allow user to provide a new picture and update it
        // convert picture name to UUID
        // how to upload picture to server (could be bytes?)

    }

    /**
     *
     * @param accountID - account ID in database, retrieved from account object
     * @param newUsername - new username that is being stored in database
     */
    public void updateUsername(int accountID, String newUsername){
        acc = Account.Find(accountID).get();
        acc.setUname(newUsername);
        try{
            acc.update();
            logger.info("Username updated successfully!");
        } catch(SQLException e){
            logger.error("Error: " + e.toString());
        }
    }

    /**
     *
     * @param accountID - account ID in database, retrieved from account object
     * @param passwordhash new hash generated from password given in form
     * @param passwordsalt new salt generated, from Encryption class
     */
    public void updatePassword(int accountID, byte[] passwordhash, byte[] passwordsalt){
        acc = Account.Find(accountID).get();
        acc.setPasswordHash(passwordhash);
        acc.setPasswordSalt(passwordsalt);
        try{
            acc.update();
            logger.info("Password updated successfully!");
        } catch(SQLException e){
            logger.error("Error: " + e.toString());
        }
    }

    /**
     *
     * @param accountID - account ID in database, retrieved from account object
     * @param newBio  updated bio given by user
     */
    public void updateUserBio(int accountID, String newBio){
       acc = Account.Find(accountID).get();
       acc.setBio(newBio);
       try{
           acc.update();
           logger.info("Bio updated successfully!");
       } catch(SQLException e){
           logger.error("Error: " + e.toString());
       }

    }
    //remove from follow add to invest!

    /**
     * @param type
     * @param ticker
     * @param numShares
     */


    public void trade(TransactionType type, String ticker, int numShares) throws IOException {
        List<Stock> results = Stock.FindCustom("SELECT curr_price"+
                "FROM stocks"+
                "WHERE symbol LIKE ?", "'%" + ticker +"'%");


        if(results.isEmpty()) {
            logger.info("ERROR: no results returned (Stock probably not on Yahoo finance)");
        }

        Optional<Stock> stockOpt = Optional.ofNullable(results.get(0));
        BigDecimal volumePrice = stockOpt.get().getCurrPrice().multiply(new BigDecimal(numShares));

        if(type.equals(TransactionType.BUY)){
            int compareRes = acc.getAccountBal().compareTo(volumePrice);
            if(compareRes !=-1){
                acc.setAccountBal(acc.getAccountBal().subtract(volumePrice));
                //need to figure out how to associate investments with account in DB and then move stock from follow to invest
            }else{
                logger.info("The user does not have the funds to cover this trade!");
            }
        }else if(type.equals(TransactionType.SELL)){
            acc.setAccountBal(acc.getAccountBal().add(volumePrice));
            //then remove the stock from invested to following
        }




    }


}
