package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.following.Follow;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
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
        if(!acc.isPresent())
        {
            return false;
        }
        // check hash and salt against login credentials
        boolean isValid = Encryption.validateInput(password.toCharArray(), acc.get().getPasswordSalt(), acc.get().getPasswordHash());

        // check if credentials are valid
        if (isValid)
        {
            logger.info("Logged user "+username+ " in successfully!");
        }
        else
        {
            logger.info("Couldn't find account with username "+username);
        }

        return isValid;
    }



    /**
     *
     * @param inputStream  file contents converted to input stream
     * @param fileName file name user uploaded
     */
    public void updateProfilePicture(InputStream inputStream, String fileName) throws IOException, SQLException {
        File saveDir = new File("./war/" + Account.getProfilePictureDirectory()); // directory where images are stored
        if(!saveDir.exists())
        {
            saveDir.mkdirs();
        }
        try
        {

            BufferedImage img = ResizeBufferedImage(ImageIO.read(inputStream), Account.ProfilePictureMaxWidth(), Account.ProfilePictureMaxHeight());

            String imgName = UUID.randomUUID().toString() + fileName.split("\\.")[0];
            File picture = new File(saveDir.getPath() + "/" + imgName + ".jpg");
            logger.info(picture.getAbsolutePath());
            ImageIO.write(img, "jpg", picture);
            acc.setProfilePicture(imgName + ".jpg");

        }catch (IOException e)
        {
            throw new IOException("Error reading image: ", e);
        }



        acc.update();
        logger.info("Profile Picture updated successfully!");
    }

    /**
     *
     * @param newUsername - new username that is being stored in database
     */
    public void updateUsername(String newUsername) {
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
        StocksFollowed temp = new StocksFollowed(Account.FindCustom("SELECT id, followed_stocks FROM account WHERE UUID = ?", acc.getUUID()).get(0).getFollowedStocks());
        temp.addFollow(new Follow(Stock.Find(ticker).get().getCurrPrice(), Stock.Find(ticker).get(), SQL.GetTimeStamp() ));
        acc.setFollowedStocks(temp.followObjectsToSting());
        acc.update();
    }
    public void unFollowStock(String ticker) throws SQLException {
        StocksFollowed temp = new StocksFollowed(Account.FindCustom("SELECT id, followed_stocks FROM account WHERE UUID = ?", acc.getUUID()).get(0).getFollowedStocks());
        if(temp.containsStock(ticker)) {
            temp.removeFollow(ticker);
            acc.setFollowedStocks(temp.followObjectsToSting());
            acc.update();
        }else{
            logger.error("Error: Control flow broke somewhere, user cannot unfollow a stock that they aren't following!~");
        }
    }

    public void trade(TransactionType type, String ticker, int numShares) throws SQLException {
        if(numShares ==-1){
            throw new TradeException("Please specify at least one stock to trade with", TradeExceptionType.NOT_ENOUGH_SHARES);
        }
        Stock localStock = Stock.Find(ticker).orElse(null);
        if(localStock == null){
            throw new TradeException("You are not following that stock! Please follow and wait for new cycle before trading",TradeExceptionType.NOT_FOLLOWING_STOCK);
        }

        if (type.equals(TransactionType.BUY)) {
            //check that the user has the funds
            int compareResult = acc.getWalletBalance().compareTo(Stock.Find(ticker).get().getCurrPrice().multiply(new BigDecimal(numShares)));
            if (compareResult != -1) {
                LinkedList<Account> queryResult =  new LinkedList<Account>(Account.FindCustom("SELECT followed_stocks, invested_stocks, transaction_history, id FROM account WHERE UUID = ?", acc.getUUID())) ;
                Account localAccount = queryResult.get(0);
                if(localAccount== null){
                    throw new TradeException("could not find user in schema", TradeExceptionType.USER_NOT_FOUND);
                }
                StocksFollowed tempStocksFollowed = new StocksFollowed(localAccount.getFollowedStocks());

                InvestmentCollection ic = new InvestmentCollection(localAccount.getInvestedStocks());

                //StocksFollowed tempStocksFollowed = new StocksFollowed(followingString);
                //if it contains the ticker, remove it from the following list
                if (tempStocksFollowed.containsStock(ticker) || ic.isInvested(ticker)) {
                    //add the stock to transactionHistory
                    //add the transaction using a method with a string
                    TransactionHistory tempTransactionHistory = new TransactionHistory(localAccount.getTransactionHistory());
                    Transaction tempTransaction = new Transaction(TransactionType.BUY, SQL.GetTimeStamp(), localStock.getCurrPrice(), numShares, localStock);
                    tempTransactionHistory.addTransaction(tempTransaction);
                    //update and push to DB
                    acc.setTransactionHistory(tempTransactionHistory.buildTransactionJSON());


                    //add the stock to investments   Exactly like transactionhistory minus the enum
                    InvestmentCollection investments = new InvestmentCollection(localAccount.getInvestedStocks());
                    Investment tempInvestment = new Investment(numShares, ticker, SQL.GetTimeStamp());
                    investments.addInvestment(tempInvestment);
                    acc.setWalletBalance(acc.getWalletBalance().subtract(new BigDecimal(numShares).multiply(localStock.getCurrPrice())));
                    //update and push to DB
                    acc.setInvestedStocks(investments.buildJSON());


                    System.out.println("debug for the StocksFollowed object ");
                    tempStocksFollowed.removeFollow(ticker);
                    for(Follow f : tempStocksFollowed.getStocksFollowed()){
                        System.out.println(f.getStock().getSymbol());
                    }
                    //update and push to DB
                    acc.setFollowedStocks(tempStocksFollowed.followObjectsToSting());
                    acc.update();
                    System.out.println("debug for string");
                    System.out.println(acc.getFollowedStocks());

                    logger.info("Transaction success!");
                } else {
                    throw new TradeException("You are not following or invested in that stock. If you want to buy shares in this company, please follow the stock first",TradeExceptionType.NOT_FOLLOWING_STOCK);
                }
            }else {
                throw new TradeException("You do not have enough funds for this purchase",TradeExceptionType.NOT_ENOUGH_FUNDS);
            }
        }else if(type.equals(TransactionType.SELL)){
            //check that the user has the particular stock they want to sell, as well as the quantity that they desire to sell
            //check that the lists are actually populated
            InvestmentCollection investmentCollection = new InvestmentCollection(Account.FindCustom("SELECT invested_stocks,id FROM account WHERE UUID = ?",acc.getUUID()).get(0).getInvestedStocks());
            StocksFollowed stocksFollowed = new StocksFollowed(Account.FindCustom("SELECT followed_stocks ,id FROM account WHERE UUID = ?",acc.getUUID()).get(0).getFollowedStocks());
            if(investmentCollection.isInvested(ticker)){
                    if(numShares == investmentCollection.getInvestment(ticker).getNumShares()){
                        //if all of the shares are sold, then remove from invested and back to follow send out to DB
                        investmentCollection.removeInvestment(ticker);
                        stocksFollowed.removeFollow(ticker);
                        acc.setFollowedStocks(stocksFollowed.followObjectsToSting());
                    }else if(numShares<investmentCollection.getInvestment(ticker).getNumShares()) {
                        //already invested, just update the number of shares
                        investmentCollection.getInvestment(ticker).setNumShares(investmentCollection.getInvestment(ticker).getNumShares()-numShares);
                    }else{
                        throw new TradeException("You do not own enough shares to complete that trade.",TradeExceptionType.NOT_ENOUGH_SHARES);
                    }
                    TransactionHistory th = new TransactionHistory(Account.FindCustom("SELECT transaction_history,id FROM account WHERE UUID = ?", acc.getUUID()).get(0).getTransactionHistory());
                    Stock stock = Stock.Find(ticker).orElse(null);
                if( stock != null) {
                    th.addTransaction(new Transaction(type, SQL.GetTimeStamp(),Stock.Find(ticker).get().getCurrPrice(),numShares, stock));
                    acc.setTransactionHistory(th.buildTransactionJSON());
                    acc.setInvestedStocks(investmentCollection.buildJSON());

                   acc.setWalletBalance(acc.getWalletBalance().add(new BigDecimal(numShares).multiply(stock.getCurrPrice())));
               }
                    acc.update();
            }else{
                throw new TradeException("You do not own any of that stock.",TradeExceptionType.NOT_INVESTED);
            }
        }

    }

    /**
     *
     * @param newBalance updated wallet balance for user
     */
    public void updateWalletBalance(BigDecimal newBalance)
    {
        // set new account balance
        acc.setWalletBalance(newBalance);

        // push to DB
        try
        {
            acc.update();
        }
        catch (SQLException e)
        {
            logger.error("Error updating balance in database.");
        }
    }






}
