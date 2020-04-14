package io.github.virtualstocksim.account;

import com.google.gson.JsonObject;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.following.Follow;
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
import java.util.UUID;


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
        File saveDir = new File("./war/userdata/ProfilePictures"); // directory where images are stored
        if(!saveDir.exists()){
            saveDir.mkdirs();
        }
        try{

            BufferedImage img = ResizeBufferedImage(ImageIO.read(inputStream), Account.ProfilePictureMaxWidth(), Account.ProfilePictureMaxHeight());

            String imgName = UUID.randomUUID().toString() + fileName.split("\\.")[0];
            File picture = new File(saveDir.getPath() + "/" + imgName + ".jpg");
            ImageIO.write(img, "jpg", picture);
            acc.setProfilePicture(imgName);

        }catch (IOException e){
            logger.error("Error reading image: " +e);
        }



        try{
            acc.update();
            logger.info("Profile Picture updated successfully!");
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

    /**
     * Resize a buffered image
     * @param image Image to be resized
     * @param width Desired width
     * @param height Desired height
     * @return Resized buffered image
     */
    private static BufferedImage ResizeBufferedImage(BufferedImage image, int width, int height)
    {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


        Graphics2D g = resized.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }

    public void followStock(String ticker) throws SQLException {
        StocksFollowed temp = new StocksFollowed(Account.FindCustom("SELECT followed_stocks FROM accounts WHERE UUID = ?", acc.getUUID()).get(0).getFollowedStocks());
        temp.addFollow(new Follow(Stock.Find(ticker).get().getCurrPrice(), Stock.Find(ticker).get(), SQL.GetTimeStamp() ));
        acc.setFollowedStocks(temp.followObjectsToSting());
        acc.update();
    }
    public void unFollowStock(String ticker) throws SQLException {
        StocksFollowed temp = new StocksFollowed(Account.FindCustom("SELECT followed_stocks FROM accounts WHERE UUID = ?", acc.getUUID()).get(0).getFollowedStocks());

        if(temp.containsStock(ticker)) {
            temp.removeFollow(ticker);
            acc.setFollowedStocks(temp.followObjectsToSting());
            acc.update();
        }else{
            logger.error("Error: Control flow broke somewhere, user cannot unfollow a stock that they aren't following!~");
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
                    tempStocksFollowed.removeFollow(ticker);

                    //update and push to DB
                    acc.setTransactionHistory(tempStocksFollowed.followObjectsToSting());
                    acc.update();

                    //add the stock to transactionHistory
                    String transHistoryString = Account.FindCustom("SELECT transaction_history FROM accounts where UUID = ? ", acc.getUUID()).get(0).getTransactionHistory();
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
                    Investment tempInvestment = new Investment(numShares, ticker, SQL.GetTimeStamp());
                    investments.addInvestment(tempInvestment);
                    //update and push to DB
                    acc.setInvestedStocks(investments.buildJSON());
                    acc.update();
                    logger.info("Transaction success!");
                } else {
                    logger.warn("Warning: the user is not following that stock!");
                }
            }else {
                logger.info("Error: The user did not have the funds to purchase this. REQUIRED: $" + Stock.Find(ticker).get().getCurrPrice().multiply(new BigDecimal(numShares)) + " has $" + acc.getWalletBalance());
            }
        }else if(type.equals(TransactionType.SELL)){
            //check that the user has the particular stock they want to sell, as well as the quantity that they desire to sell
            InvestmentCollection investmentCollection = new InvestmentCollection(Account.FindCustom("SELECT invested_stocks FROM accounts WHERE UUID = ?",acc.getUUID()).get(0).getInvestedStocks());
            StocksFollowed stocksFollowed = new StocksFollowed(Account.FindCustom("SELECT followed_stocks FROM accounts WHERE UUID = ?",acc.getUUID()).get(0).getFollowedStocks());
            if(investmentCollection.isInvested(ticker)){
                if(investmentCollection.getInvestment(ticker).getNumShares()<= numShares){

                    if(numShares == investmentCollection.getInvestment(ticker).getNumShares()){
                        //if all of the shares are sold, then remove from invested and back to follow send out to DB
                        investmentCollection.removeInvestment(ticker);
                        stocksFollowed.addFollow(new Follow(Stock.Find(ticker).get().getCurrPrice(),Stock.Find(ticker).get(),SQL.GetTimeStamp()));
                        acc.setFollowedStocks(stocksFollowed.followObjectsToSting());
                    }else {
                        //The addinvestment method will handle checking if the user is already invested, and just update that number if that is the case
                        //No need to add checks here
                        investmentCollection.addInvestment(new Investment(numShares, ticker, SQL.GetTimeStamp()));
                    }

                    TransactionHistory th = new TransactionHistory(Account.FindCustom("SELECT transaction_history FROM accounts WHERE UUID = ?", acc.getUUID()).get(0).getTransactionHistory());
                    th.addTransaction(new Transaction(type, SQL.GetTimeStamp(),Stock.Find(ticker).get().getCurrPrice(),numShares, Stock.Find(ticker).get()));
                    acc.setTransactionHistory(th.buildTransactionJSON());
                    acc.setInvestedStocks(investmentCollection.buildJSON());
                    acc.update();
                }else{
                    logger.error("The associated account does not have that many shares to sell. TRANSACTION CANCELLED!");
                }
            }else{
                logger.error("The associated account does not own any of that stock. TRANSACTION CANCELLED!");
            }
        }

    }




}
