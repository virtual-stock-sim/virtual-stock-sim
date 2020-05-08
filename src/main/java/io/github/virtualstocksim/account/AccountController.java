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
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class AccountController {
    // account instance
    private Account acc;
    private static final Logger logger = LoggerFactory.getLogger(Account.class);

    public AccountController() {

    }


    public Account getModel() {
        return this.acc;
    }

    public void setModel(Account acc) {
        this.acc = acc;
    }

    /**
     * @param username username provided
     * @param password password provided - will be hashed and checked against that stored in database
     * @return Account found with specified username and password parameters, if any
     */
    public static boolean login(String username, String password) {
        logger.info("Logging user " + username + " in...");

        Optional<Account> acc = Account.Find(username);
        if (!acc.isPresent()) {
            return false;
        }
        // check hash and salt against login credentials
        boolean isValid = Encryption.validateInput(password.toCharArray(), acc.get().getPasswordSalt(), acc.get().getPasswordHash());

        // check if credentials are valid
        if (isValid) {
            logger.info("Logged user " + username + " in successfully!");
        } else {
            logger.info("Couldn't find account with username " + username);
        }

        return isValid;
    }


    /**
     * @param inputStream file contents converted to input stream
     * @param fileName    file name user uploaded
     */
    public void updateProfilePicture(InputStream inputStream, String fileName) throws IOException, SQLException {
        File saveDir = new File("./war/" + Account.getProfilePictureDirectory()); // directory where images are stored
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        try {

            BufferedImage img = ResizeBufferedImage(ImageIO.read(inputStream), Account.ProfilePictureMaxWidth(), Account.ProfilePictureMaxHeight());

            String imgName = UUID.randomUUID().toString() + fileName.split("\\.")[0];
            File picture = new File(saveDir.getPath() + "/" + imgName + ".jpg");
            logger.info(picture.getAbsolutePath());
            ImageIO.write(img, "jpg", picture);
            acc.setProfilePicture(imgName + ".jpg");

        } catch (IOException e) {
            throw new IOException("Error reading image: ", e);
        }


        acc.update();
        logger.info("Profile Picture updated successfully!");
    }

    /**
     * @param newUsername - new username that is being stored in database
     */
    public void updateUsername(String newUsername) {
        // change username in model
        acc.setUsername(newUsername);

        // update username in db
        try {
            acc.update();
            logger.info("Username updated successfully!");
        } catch (SQLException e) {
            logger.error("Error: " + e.toString());
        }
    }

    /**
     * @param password password given by user to be hashed and stored in db
     */
    public void updatePassword(String password) {
        // generate new hash and salt
        byte[] newSalt = Encryption.getNextSalt();
        byte[] newHash = Encryption.hash(password.toCharArray(), newSalt);

        // update account with newly created hash and salt
        acc.setPasswordHash(newHash);
        acc.setPasswordSalt(newSalt);

        /* update account in database */
        try {
            acc.update();
            logger.info("Password updated successfully!");
        } catch (SQLException e) {
            logger.error("Error: " + e.toString());
        }
    }

    /**
     * @param newBio updated bio given by user to be stored in DB
     */
    public void updateUserBio(String newBio) {
        acc.setBio(newBio);
        try {
            acc.update();
            logger.info("Bio updated successfully!");
        } catch (SQLException e) {
            logger.error("Error: " + e.toString());
        }

    }

    /**
     * Resize a buffered image
     *
     * @param image  Image to be resized
     * @param width  Desired width
     * @param height Desired height
     * @return Resized buffered image
     */
    private static BufferedImage ResizeBufferedImage(BufferedImage image, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


        Graphics2D g = resized.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }

    /**
     *
     * @param symbol symbol of stock to follow
     * @throws SQLException if an error occurs updating in DB
     */
    public void followStock(String symbol) throws SQLException {
        Stock localStock = Stock.Find(symbol).orElse(null);
        if (localStock == null) {
            throw new TradeException("That stock could not be found in the schema. Please check that the symbol is formatted correctly", TradeExceptionType.STOCK_NOT_FOUND);
        }
        List<Account> queryResult = Account.FindCustom("SELECT id, followed_stocks FROM account WHERE UUID = ?", acc.getUUID());
        if (queryResult.isEmpty()) {
            throw new TradeException("User not found", TradeExceptionType.USER_NOT_FOUND);
        }
        Account localAccount = queryResult.get(0);

        if (queryResult.get(0) == null) {
            throw new TradeException("stock not found", TradeExceptionType.STOCK_NOT_FOUND);
        }
        StocksFollowed followed = new StocksFollowed(localAccount.getFollowedStocks());
        followed.addFollow(new Follow(localStock.getCurrPrice(), localStock, SQL.GetTimeStamp()));
        acc.setFollowedStocks(followed.followObjectsToSting());
        acc.update();
    }

    /**
     *
     * @param symbol - Symbol of stock to unfollow
     * @throws SQLException if an error occurs updating in DB
     */
    public void unFollowStock(String symbol) throws SQLException {
        List<Account> accounts = Account.FindCustom("SELECT id, followed_stocks FROM account WHERE UUID = ?", acc.getUUID());
        
        if (accounts.isEmpty() || accounts.get(0) == null)
        {
            throw new TradeException("User not found", TradeExceptionType.USER_NOT_FOUND);
        }

        StocksFollowed followed = new StocksFollowed(accounts.get(0).getFollowedStocks());
        if (followed.containsStock(symbol))
        {
            followed.removeFollow(symbol);
            acc.setFollowedStocks(followed.followObjectsToSting());
            acc.update();
        }
        else
        {
            logger.error("Error: Control flow broke somewhere, user cannot unfollow a stock that they aren't following!");
        }
    }

    /**
     * Called when a user is unfollowing a stock and (may) still owns shares in it
     * @param symbol Symbol of stock to uninvest
     * @throws SQLException
     */
    public void unInvest(String symbol) throws SQLException {
        List<Account> accounts = Account.FindCustom("SELECT id, invested_stocks FROM account WHERE UUID = ?", acc.getUUID());

        // check if user exists
        if (accounts.isEmpty() || accounts.get(0) == null)
        {
            throw new TradeException("User not found", TradeExceptionType.USER_NOT_FOUND);
        }

        // get investments to retrieve shares
        InvestmentCollection investmentCollection = new InvestmentCollection(accounts.get(0).getInvestedStocks());
        int sharesToSell = investmentCollection.getInvestment(symbol).getNumShares();

        if(sharesToSell > 0)
        {
            // sell all current shares of given stock, if any
            trade(TransactionType.SELL, symbol, sharesToSell);
        }

    }

    /**
     *
     * @param type Buy or Sell
     * @param symbol Symbol of stock
     * @param numShares Shares that user is buying or selling
     * @throws SQLException If an error occurs updating in DB
     */
    public void trade(TransactionType type, String symbol, int numShares) throws SQLException {
        if (numShares == -1) {
            throw new TradeException("Please specify at least one stock to trade with", TradeExceptionType.NOT_ENOUGH_SHARES);
        }
        Stock localStock = Stock.Find(symbol).orElse(null);
        if (localStock == null) {
            throw new TradeException("You are not following that stock! Please follow and wait for new cycle before trading", TradeExceptionType.NOT_FOLLOWING_STOCK);
        }

        if (type.equals(TransactionType.BUY)) {
            //check that the user has the funds
            int compareResult = acc.getWalletBalance().compareTo(localStock.getCurrPrice().multiply(new BigDecimal(numShares)));
            if (compareResult != -1) {
                List<Account> queryResult = Account.FindCustom("SELECT followed_stocks, invested_stocks, transaction_history, id FROM account WHERE UUID = ?", acc.getUUID());
                if (queryResult.isEmpty()) {
                    throw new TradeException("Could not find user in schema!", TradeExceptionType.USER_NOT_FOUND);
                }
                Account localAccount = queryResult.get(0);
                if (localAccount == null) {
                    throw new TradeException("could not find user in schema", TradeExceptionType.USER_NOT_FOUND);
                }
                StocksFollowed tempStocksFollowed = new StocksFollowed(localAccount.getFollowedStocks());

                InvestmentCollection ic = new InvestmentCollection(localAccount.getInvestedStocks());

                //StocksFollowed tempStocksFollowed = new StocksFollowed(followingString);
                //if it contains the symbol, remove it from the following list
                if (tempStocksFollowed.containsStock(symbol) || ic.isInvested(symbol)) {
                    //add the stock to transactionHistory
                    //add the transaction using a method with a string
                    TransactionHistory tempTransactionHistory = new TransactionHistory(localAccount.getTransactionHistory());
                    Transaction tempTransaction = new Transaction(TransactionType.BUY, SQL.GetTimeStamp(), localStock.getCurrPrice(), numShares, localStock);
                    tempTransactionHistory.addTransaction(tempTransaction);
                    //update and push to DB
                    acc.setTransactionHistory(tempTransactionHistory.buildTransactionJSON());


                    //add the stock to investments   Exactly like transactionhistory minus the enum
                    InvestmentCollection investments = new InvestmentCollection(localAccount.getInvestedStocks());
                    Investment tempInvestment = new Investment(numShares, localStock, SQL.GetTimeStamp());
                    investments.addInvestment(tempInvestment);
                    acc.setWalletBalance(acc.getWalletBalance().subtract(new BigDecimal(numShares).multiply(localStock.getCurrPrice())));
                    //update and push to DB
                    acc.setInvestedStocks(investments.buildJSON());


                    System.out.println("debug for the StocksFollowed object ");
                    tempStocksFollowed.removeFollow(symbol);
                    for (Follow f : tempStocksFollowed.getStocksFollowed()) {
                        System.out.println(f.getStock().getSymbol());
                    }
                    //update and push to DB
                    acc.setFollowedStocks(tempStocksFollowed.followObjectsToSting());
                    acc.update();
                    System.out.println("debug for string");
                    System.out.println(acc.getFollowedStocks());

                    logger.info("Transaction success!");
                } else {
                    throw new TradeException("You are not following or invested in that stock. If you want to buy shares in this company, please follow the stock first", TradeExceptionType.NOT_FOLLOWING_STOCK);
                }
            } else {
                throw new TradeException("You do not have enough funds for this purchase", TradeExceptionType.NOT_ENOUGH_FUNDS);
            }
        } else if (type.equals(TransactionType.SELL)) {
            //check that the user has the particular stock they want to sell, as well as the quantity that they desire to sell
            //check that the lists are actually populated
            List<Account> queryResult = Account.FindCustom("SELECT invested_stocks, transaction_history, followed_stocks, id FROM account WHERE UUID = ?", acc.getUUID());
            if (queryResult.isEmpty() || queryResult.get(0) == null) {
                throw new TradeException("User not found", TradeExceptionType.USER_NOT_FOUND);
            }
            Account localAccount = queryResult.get(0);
            InvestmentCollection investmentCollection = new InvestmentCollection(localAccount.getInvestedStocks());
            StocksFollowed stocksFollowed = new StocksFollowed(localAccount.getFollowedStocks());

            if (investmentCollection.isInvested(symbol)) {
                if (numShares == investmentCollection.getInvestment(symbol).getNumShares()) {
                    //if all of the shares are sold, then remove from invested and back to follow send out to DB
                    investmentCollection.removeInvestment(symbol);
                    stocksFollowed.setFollow( new Follow(localStock.getCurrPrice(),localStock,SQL.GetTimeStamp()));
                    acc.setFollowedStocks(stocksFollowed.followObjectsToSting());
                } else if (numShares < investmentCollection.getInvestment(symbol).getNumShares()) {
                    //already invested, just update the number of shares
                    investmentCollection.getInvestment(symbol).setNumShares(investmentCollection.getInvestment(symbol).getNumShares() - numShares);
                } else {
                    throw new TradeException("You do not own enough shares to complete that trade.", TradeExceptionType.NOT_ENOUGH_SHARES);
                }

                TransactionHistory th = new TransactionHistory(localAccount.getTransactionHistory());
                Stock stock = Stock.Find(symbol).orElse(null);
                if (stock != null) {
                    th.addTransaction(new Transaction(type, SQL.GetTimeStamp(), Stock.Find(symbol).get().getCurrPrice(), numShares, stock));
                    acc.setTransactionHistory(th.buildTransactionJSON());
                    acc.setInvestedStocks(investmentCollection.buildJSON());

                    acc.setWalletBalance(acc.getWalletBalance().add(new BigDecimal(numShares).multiply(stock.getCurrPrice())));
                }
                acc.update();
            } else {
                throw new TradeException("You do not own any of that stock.", TradeExceptionType.NOT_INVESTED);
            }
        }

    }

    /**
     * @param newBalance updated wallet balance for user
     */
    public void updateWalletBalance(BigDecimal newBalance) {
        // set new account balance
        acc.setWalletBalance(newBalance);

        // push to DB
        try {
            acc.update();
        } catch (SQLException e) {
            logger.error("Error updating balance in database.");
        }
    }

    /**
     * Reset user's followed stocks
     */
    public void resetFollowed() {
        acc.setFollowedStocks("");
        // push to DB
        try {
            acc.update();
        } catch (SQLException e) {
            logger.error("Error updating followed stocks in database.");
        }
    }

    /**
     * Reset User's transaction history
     */
    public void resetTransactionHistory(){
        acc.setTransactionHistory("");
        // push to DB
        try {
            acc.update();
        } catch (SQLException e) {
            logger.error("Error updating transaction history in database.");
        }
    }

    /**
     * Opt into investor leaderboard
     */
    public void optInToLeaderboard(){
        acc.setLeaderboardRank(0);

        try {
            acc.update();
        } catch (SQLException e) {
            logger.error("Error updating leaderboard rank in database.");
        }
    }

    /**
     * Opt out of investor leaderboard
     */
    public void optOutOfLeaderboard(){
        acc.setLeaderboardRank(-1);

        try {
            acc.update();
        } catch (SQLException e) {
            logger.error("Error updating leaderboard rank in database.");
        }
    }

    /**
     *
     * @param newEmail Updated email from user
     */
    public void resetEmail(String newEmail){
        acc.setEmail(newEmail);

        try {
            acc.update();
        } catch (SQLException e) {
            logger.error("Error updating email in database.");
        }
    }

}
