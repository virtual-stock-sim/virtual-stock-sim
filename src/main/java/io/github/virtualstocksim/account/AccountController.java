package io.github.virtualstocksim.account;

import com.google.gson.JsonObject;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.sql.rowset.CachedRowSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;


public class AccountController {
    // account instance
    private Account acc;
    private static final Logger logger = LoggerFactory.getLogger(Account.class);

    public AccountController() {

    }


    public Account getModel(){
        return this.acc;
    }

    public void setModel (Account acc){
        this.acc=acc;
    }

    /**
     *
     * @param username username provided
     * @param password  password provided - will be hashed and checked against that stored in database
     * @return Account found with specified username and password parameters, if any
     */
    public static boolean login(String username, String password)
    {
        logger.info("Logging user " + username + " in...");

        Optional<Account> acc = Account.Find(username);
        if(!acc.isPresent()){
            return false;
        }
        // check hash and salt against login credentials
        boolean isValid = Encryption.validateInput(password.toCharArray(), acc.get().getPasswordSalt(), acc.get().getPasswordHash());

        // check if credentials are valid
        if (isValid) {
            logger.info("Logged user "+username+ " in successfully!");
        }else{
            logger.info("Couldn't find account with username "+username);
        }

        return isValid;
    }



    /**
     *
     * @param inputStream  file contents converted to input stream
     * @param fileName file name user uploaded
     */
    public void updateProfilePicture(InputStream inputStream, String fileName) {
        File uploadDir = new File("./userdata/ProfilePictures"); // directory where images are stored
        if(!uploadDir.exists()){
            uploadDir.mkdirs();
        }
        try{
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            ImageIO.write(bufferedImage, fileName.substring(fileName.lastIndexOf("."), fileName.length()-1), uploadDir);

        }catch (IOException e){
            logger.error("Error reading image: " +e);
        }



        try{
            acc.update();
            logger.info("ProfilePicture updated successfully!");
        } catch(SQLException e){
            logger.error("Error: " + e.toString());
        }

    }

    /**
     *
     * @param newUsername - new username that is being stored in database
     */
    public void updateUsername(String newUsername){
        // change username in model
        acc.setUsername(newUsername);

        // update username in db
        try{
            acc.update();
            logger.info("Username updated successfully!");
        } catch(SQLException e){
            logger.error("Error: " + e.toString());
        }
    }

    /**
     *
     * @param password password given by user to be hashed and stored in db
     */
    public void updatePassword(String password){
        // generate new hash and salt
        byte[] newSalt = Encryption.getNextSalt();
        byte[] newHash = Encryption.hash(password.toCharArray(),newSalt);

        // update account with newly created hash and salt
        acc.setPasswordHash(newHash);
        acc.setPasswordSalt(newSalt);

        /* update account in database */
        try{
            acc.update();
            logger.info("Password updated successfully!");
        } catch(SQLException e){
            logger.error("Error: " + e.toString());
        }
    }

    /**
     *
     * @param newBio  updated bio given by user to be stored in DB
     */
    public void updateUserBio(String newBio){
       acc.setBio(newBio);
       try{
           acc.update();
           logger.info("Bio updated successfully!");
       } catch(SQLException e){
           logger.error("Error: " + e.toString());
       }

    }


    public void trade(TransactionType type, String ticker, int numShares) throws SQLException {
        if (type.equals(TransactionType.BUY)) {
            //check that the user has the funds
            int compareResult = acc.getWalletBalance().compareTo(Stock.Find(ticker).get().getCurrPrice().multiply(new BigDecimal(numShares)));
            if (compareResult != -1) {
                String followingString = Account.FindCustom("SELECT followed_stocks FROM accounts WHERE UUID = ?", acc.getUUID()).get(0).getFollowedStocks();
                StocksFollowed tempStocksFollowed = new StocksFollowed(followingString);
                //if it contains the ticker, remove it from the following list
                if (tempStocksFollowed.containsStock(ticker)) {
                    tempStocksFollowed.removeFollow(tempStocksFollowed.getIndexofStock(ticker));
                }
                //update and push to DB
                acc.setTransactionHistory(tempStocksFollowed.followObjectsToSting());
                acc.update();

                //add the stock to transactionHistory
                String transHistoryString = Account.FindCustom("SELECT transaction_history FROM accounts where UUID = ? ",acc.getUUID()).get(0).getTransactionHistory();
                //add the transaction using a method with a string
                TransactionHistory tempTransactionHistory = new TransactionHistory(transHistoryString);
                Transaction tempTransaction = new Transaction(TransactionType.BUY, SQL.GetTimeStamp(), Stock.Find(ticker).get().getCurrPrice(), numShares, Stock.Find(ticker).get());
                tempTransactionHistory.addTransaction(tempTransaction);
                //update and push to DB
                acc.setTransactionHistory(tempTransactionHistory.buildTransactionJSON());
                acc.update();

                //add the stock to investments   Exactly like transactionhistory minus the enum
                String investedStocks = Account.FindCustom("SELECT invested_stocks FROM accounts where UUID = ?", acc.getUUID()).get(0).getInvestedStocks();
                InvestmentCollection investments = new InvestmentCollection(investedStocks);
                Investment tempInvestment = new Investment(numShares,ticker,SQL.GetTimeStamp());
                investments.addInvestment(tempInvestment);
                //update and push to DB
                acc.setInvestedStocks(investments.buildJSON());
                acc.update();


            } else {
                logger.warn("Warning: the user is not following that stock!");
            }
            logger.info("Error: The user did not have the funds to purchase this. REQUIRED: $" + Stock.Find(ticker).get().getCurrPrice().multiply(new BigDecimal(numShares)) + " has $" + acc.getWalletBalance());
        }
        //TODO:integrate sell with the account &database
    }



}
