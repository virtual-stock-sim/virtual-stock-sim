package io.github.virtualstocksim.account;

import com.google.gson.JsonParseException;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.following.FollowedStock;
import io.github.virtualstocksim.following.FollowedStocks;
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
    private Account account;
    private FollowedStocks followedStocks;
    private static final Logger logger = LoggerFactory.getLogger(Account.class);

    public AccountController() {

    }


    public Account getModel()
    {
        return this.account;
    }

    /**
     * Set the model
     * @param account Account to set the model as
     * @throws NullPointerException If account is null
     * @throws JsonParseException If the account's followed stocks couldn't be parsed
     */
    public void setModel(Account account) throws NullPointerException, JsonParseException
    {
        if(account == null)
            throw new NullPointerException("Account can't be null");

        this.account = account;
        followedStocks = new FollowedStocks(account.getFollowedStocks());
    }

    /**
     * @param username username provided
     * @param password password provided - will be hashed and checked against that stored in database
     * @return Account found with specified username and password parameters, if any
     */
    public boolean login(String username, String password) {
        logger.info("Logging user " + username + " in...");

        // check hash and salt against login credentials
        boolean isValid = Encryption.validateInput(password.toCharArray(), account.getPasswordSalt(), account.getPasswordHash());

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
            account.setProfilePicture(imgName + ".jpg");

        } catch (IOException e) {
            throw new IOException("Error reading image: ", e);
        }


        account.update();
        logger.info("Profile Picture updated successfully!");
    }

    /**
     * @param newUsername - new username that is being stored in database
     */
    public void updateUsername(String newUsername) {
        // change username in model
        account.setUsername(newUsername);

        // update username in db
        try {
            account.update();
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
        account.setPasswordHash(newHash);
        account.setPasswordSalt(newSalt);

        /* update account in database */
        try {
            account.update();
            logger.info("Password updated successfully!");
        } catch (SQLException e) {
            logger.error("Error: " + e.toString());
        }
    }

    /**
     * @param newBio updated bio given by user to be stored in DB
     */
    public void updateUserBio(String newBio) {
        account.setBio(newBio);
        try {
            account.update();
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
     * Add a stock to list of followed stocks for this controller's account model
     * @param stock Stock to follow
     * @throws SQLException Exception while committing the account changes to the database
     */
    public void followStock(Stock stock) throws SQLException
    {
        FollowedStock followedStock = new FollowedStock(stock, stock.getCurrPrice(), SQL.GetTimeStamp());
        followedStocks.addFollowedStock(followedStock);

        account.setFollowedStocks(String.valueOf(followedStocks.asJsonArray()));
        account.update();
    }

    /**
     * Remove a stock from the list of followed stocks for this controller's account model
     * @param stockSymbol Stock to unfollow
     * @throws SQLException Exception while committing the account changes to the database
     */
    public void unfollowStock(String stockSymbol) throws SQLException
    {
        followedStocks.removeFollowedStock(stockSymbol);
        account.setFollowedStocks(String.valueOf(followedStocks.asJsonArray()));
        account.update();
    }

    /**
     * Is the account following this stock
     * @param stockSymbol Stock symbol to search for
     * @return If the account is following the stock
     */
    public boolean isFollowingStock(String stockSymbol)
    {
        return followedStocks.contains(stockSymbol);
    }

    /**
     * Get a followed stock if it exists
     * @param stockSymbol Stock symbol to search for
     * @return An optional containing the FollowedStock object if found, otherwise Optional.empty()
     */
    public Optional<FollowedStock> getFollowedStock(String stockSymbol)
    {
        return followedStocks.getFollowedStock(stockSymbol);
    }

    /**
     * Get the internal followed stocks object
     * @return FollowedStocks object
     */
    public FollowedStocks getFollowedStocks()
    {
        return followedStocks;
    }

    /**
     * Called when a user is unfollowing a stock and (may) still owns shares in it
     * @param symbol Symbol of stock to uninvest
     * @throws SQLException
     */
    public void unInvest(String symbol) throws SQLException, TradeException {
        List<Account> accounts = Account.FindCustom("SELECT id, invested_stocks FROM account WHERE UUID = ?", account.getUUID());

        // check if user exists
        if (accounts.isEmpty() || accounts.get(0) == null)
        {
            throw new TradeException("User not found", TradeExceptionType.USER_NOT_FOUND);
        }

        // get investments to retrieve shares
        InvestmentCollection investmentCollection = new InvestmentCollection(accounts.get(0).getInvestedStocks());
        Investment investment = investmentCollection.getInvestment(symbol);
        if(investment == null)
            throw new TradeException("User is not currently invested in: " + symbol);

        int sharesToSell = investment.getNumShares();

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
    public void trade(TransactionType type, String symbol, int numShares) throws SQLException, TradeException {
        if (numShares == -1) {
            throw new TradeException("Please specify at least one stock to trade with", TradeExceptionType.NOT_ENOUGH_SHARES);
        }
        Stock localStock = Stock.Find(symbol).orElse(null);
        if (localStock == null) {
            throw new TradeException("You are not following that stock! Please follow and wait for new cycle before trading", TradeExceptionType.NOT_FOLLOWING_STOCK);
        }

        if (type.equals(TransactionType.BUY)) {
            //check that the user has the funds
            int compareResult = account.getWalletBalance().compareTo(localStock.getCurrPrice().multiply(new BigDecimal(numShares)));
            if (compareResult != -1) {
                List<Account> queryResult = Account.FindCustom("SELECT followed_stocks, invested_stocks, transaction_history, id FROM account WHERE UUID = ?", account.getUUID());
                if (queryResult.isEmpty()) {
                    throw new TradeException("Could not find user in schema!", TradeExceptionType.USER_NOT_FOUND);
                }
                Account localAccount = queryResult.get(0);
                if (localAccount == null) {
                    throw new TradeException("could not find user in schema", TradeExceptionType.USER_NOT_FOUND);
                }
                FollowedStocks tempFollowedStocks = new FollowedStocks(localAccount.getFollowedStocks());

                InvestmentCollection ic = new InvestmentCollection(localAccount.getInvestedStocks());

                //StocksFollowed tempStocksFollowed = new StocksFollowed(followingString);
                //if it contains the symbol, remove it from the following list
                if (tempFollowedStocks.contains(symbol) || ic.isInvested(symbol)) {
                    //add the stock to transactionHistory
                    //add the transaction using a method with a string
                    TransactionHistory tempTransactionHistory = new TransactionHistory(localAccount.getTransactionHistory());
                    Transaction tempTransaction = new Transaction(TransactionType.BUY, SQL.GetTimeStamp(), localStock.getCurrPrice(), numShares, localStock);
                    tempTransactionHistory.addTransaction(tempTransaction);
                    //update and push to DB
                    account.setTransactionHistory(tempTransactionHistory.buildTransactionJSON());


                    //add the stock to investments   Exactly like transactionhistory minus the enum
                    InvestmentCollection investments = new InvestmentCollection(localAccount.getInvestedStocks());
                    Investment tempInvestment = new Investment(numShares, localStock, SQL.GetTimeStamp());
                    investments.addInvestment(tempInvestment);
                    account.setWalletBalance(
                            account.getWalletBalance().subtract(new BigDecimal(numShares).multiply(localStock.getCurrPrice())));
                    //update and push to DB
                    account.setInvestedStocks(investments.buildJSON());


                    unfollowStock(symbol);

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
            List<Account> queryResult = Account.FindCustom("SELECT invested_stocks, transaction_history, followed_stocks, id FROM account WHERE UUID = ?", account.getUUID());
            if (queryResult.isEmpty() || queryResult.get(0) == null) {
                throw new TradeException("User not found", TradeExceptionType.USER_NOT_FOUND);
            }
            Account localAccount = queryResult.get(0);
            InvestmentCollection investmentCollection = new InvestmentCollection(localAccount.getInvestedStocks());
            FollowedStocks followedStocks = new FollowedStocks(localAccount.getFollowedStocks());

            if (investmentCollection.isInvested(symbol)) {
                if (numShares == investmentCollection.getInvestment(symbol).getNumShares()) {
                    //if all of the shares are sold, then remove from invested and back to follow send out to DB
                    investmentCollection.removeInvestment(symbol);
                    followedStocks.addFollowedStock(new FollowedStock(localStock, localStock.getCurrPrice(), SQL.GetTimeStamp()));
                    account.setFollowedStocks(String.valueOf(followedStocks.asJsonArray()));
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
                    account.setTransactionHistory(th.buildTransactionJSON());
                    account.setInvestedStocks(investmentCollection.buildJSON());

                    account.setWalletBalance(
                            account.getWalletBalance().add(new BigDecimal(numShares).multiply(stock.getCurrPrice())));
                }
                account.update();
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
        account.setWalletBalance(newBalance);

        // push to DB
        try {
            account.update();
        } catch (SQLException e) {
            logger.error("Error updating balance in database.");
        }
    }

    /**
     * Reset user's followed stocks
     */
    public void resetFollowed() {
        account.setFollowedStocks("");
        // push to DB
        try {
            account.update();
        } catch (SQLException e) {
            logger.error("Error updating followed stocks in database.");
        }
    }

    /**
     * Reset User's transaction history
     */
    public void resetTransactionHistory(){
        account.setTransactionHistory("");
        // push to DB
        try {
            account.update();
        } catch (SQLException e) {
            logger.error("Error updating transaction history in database.");
        }
    }

    /**
     * Opt into investor leaderboard
     */
    public void optInToLeaderboard(){
        account.setLeaderboardRank(0);

        try {
            account.update();
        } catch (SQLException e) {
            logger.error("Error updating leaderboard rank in database.");
        }
    }

    /**
     * Opt out of investor leaderboard
     */
    public void optOutOfLeaderboard(){
        account.setLeaderboardRank(-1);

        try {
            account.update();
        } catch (SQLException e) {
            logger.error("Error updating leaderboard rank in database.");
        }
    }

    /**
     *
     * @param newEmail Updated email from user
     */
    public void resetEmail(String newEmail){
        account.setEmail(newEmail);

        try {
            account.update();
        } catch (SQLException e) {
            logger.error("Error updating email in database.");
        }
    }

}
