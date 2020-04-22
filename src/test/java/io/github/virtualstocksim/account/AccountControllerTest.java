package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.following.Follow;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.DummyStocks;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.*;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.Assert.*;

public class AccountControllerTest
{
    private static final Logger logger = LoggerFactory.getLogger(AccountControllerTest.class);
    private AccountController conn;
    private Account acct;
    StocksFollowed stocksFollowed;
    InvestmentCollection investmentCollection;
    Stock Amazon;
    List<Transaction> transactions;
    TransactionHistory transactionHistory;
    List<Stock> stocks;
    String uuid;

    private static final String TEST_PASSWORD = "virtualstocksim";

    @Before
    public void setup() throws SQLException {
        conn = new AccountController();
        uuid = UUID.randomUUID().toString();
        stocks = new LinkedList<Stock>();

        //allows test to be run multiple times without key duplication, or old data messing up tests
        try(Connection connection = AccountDatabase.getConnection())
        {
            SQL.executeUpdate(connection, "DELETE FROM account WHERE username = ? ", "TestAdmin");
            SQL.executeUpdate(connection, "DELETE FROM account WHERE username = ? ", "DanPalm5");

        }
        catch (SQLException e )
        {
            fail();
        }

        conn.setModel(Account.Create("TestAdmin", "test@vss.com", TEST_PASSWORD, AccountType.ADMIN).get());


        List <Follow>followList = new LinkedList<Follow>();
        //followList.add(new Follow(new BigDecimal(200), Stock.Find("AMZN").get(),SQL.GetTimeStamp()));
        //followList.add(new Follow(new BigDecimal(600), Stock.Find("TSLA").get(),SQL.GetTimeStamp()));
        followList.add(new Follow(new BigDecimal(23), Stock.Find("GOOGL").get(),SQL.GetTimeStamp()));
        followList.add(new Follow(new BigDecimal(12), Stock.Find("F").get(),SQL.GetTimeStamp()));
       // followList.add(new Follow(new BigDecimal(100), Stock.Find("BDX").get(),SQL.GetTimeStamp())); //add later to test addInvestment
        stocksFollowed = new StocksFollowed(followList);
        conn.getModel().setFollowedStocks(stocksFollowed.followObjectsToSting());

        List investmentList = new LinkedList<>();
        investmentList.add(new Investment(12,"AMZN",SQL.GetTimeStamp()));
        investmentList.add(new Investment(5, "TSLA",SQL.GetTimeStamp()));
        //will add google and ford to investments in later test
        investmentCollection = new InvestmentCollection(investmentList);
        conn.getModel().setInvestedStocks(investmentCollection.buildJSON());

        conn.getModel().setWalletBalance(new BigDecimal(100000.00));

        transactions = new LinkedList<Transaction>();

        transactions.add(new Transaction(TransactionType.BUY,Timestamp.valueOf("1977-08-05 03:03:12.000000000"),new BigDecimal("1055.00"),20, Stock.Find("AMZN").get()));
        transactions.add(new Transaction(TransactionType.SELL,Timestamp.valueOf("1977-08-05 03:03:12.000000000"),new BigDecimal("2045.00"),8, Stock.Find("AMZN").get()));
        transactions.add(new Transaction(TransactionType.BUY,SQL.GetTimeStamp(),new BigDecimal("1800.00"),5, Stock.Find("TSLA").get()));
        transactionHistory = new TransactionHistory(transactions);

        conn.getModel().setTransactionHistory(transactionHistory.buildTransactionJSON());

        conn.getModel().update();

    }

    @Test
    public void testGetModel() {
        Optional<Account> find_acc = Account.Find("TestAdmin");
        if(!find_acc.isPresent()){ fail(); }
        assertEquals(find_acc.get().getUUID(),conn.getModel().getUUID());
        assertEquals(find_acc.get().getType().getText(),conn.getModel().getType().getText());
        assertEquals(find_acc.get().getEmail(),conn.getModel().getEmail());
        assertArrayEquals(find_acc.get().getPasswordHash(), conn.getModel().getPasswordHash());
        assertArrayEquals(find_acc.get().getPasswordSalt(), conn.getModel().getPasswordSalt());
        assertEquals(find_acc.get().getCreationDate(),conn.getModel().getCreationDate());
        assertEquals(find_acc.get().getLeaderboardRank(),conn.getModel().getLeaderboardRank());
        assertEquals(find_acc.get().getBio(),conn.getModel().getBio());
        assertEquals(find_acc.get().getProfilePicture(),conn.getModel().getProfilePicture());

    }

    @Test
    public void testLogin() {
        assertTrue((AccountController.login(conn.getModel().getUsername(), TEST_PASSWORD)));

    }

    @Test
    public void testUpdateBio() {
        conn.updateUserBio("Software Engineer");
        assertEquals(conn.getModel().getBio(), "Software Engineer");
    }
    //These two tests fail because of key constraints

    @Test
    public void testUpdateUsername() {
        conn.updateUsername("DanPalm5");
        assertEquals(conn.getModel().getUsername(), "DanPalm5");
    }

    @Test
    public void testUpdatePassword(){
        conn.updatePassword("ultrasecret");
        Optional<Account> find_acc = Account.Find("TestAdmin");
        if(!find_acc.isPresent()){ fail(); }
        byte[] salt = find_acc.get().getPasswordSalt();
        byte[] hash = find_acc.get().getPasswordHash();
        byte[] pwordHash = Encryption.hash("ultrasecret".toCharArray(), salt);
        assertArrayEquals(hash, pwordHash);
    }
    //Tests just the buy portion of Trade, since it is such a big method
    @Test
    public void testBuy() throws SQLException, TradeException {
        TransactionHistory transactionHistory = new TransactionHistory(conn.getModel().getTransactionHistory());
        InvestmentCollection investmentCollection = new InvestmentCollection(conn.getModel().getInvestedStocks());
        StocksFollowed stocksFollowed = new StocksFollowed(conn.getModel().getFollowedStocks());
        int init_size = investmentCollection.getInvestments().size();
        int initial_num_transactions = transactionHistory.getTransactions().size();

        //buy shares that account already has shares in
        assertTrue(investmentCollection.isInvested("AMZN"));
        assertEquals(investmentCollection.getInvestment("AMZN").getNumShares(), 12);

        assertTrue(investmentCollection.isInvested("TSLA"));
        assertEquals(investmentCollection.getInvestment("TSLA").getNumShares(),5);

        conn.trade(TransactionType.BUY, "AMZN", 10);
        conn.trade(TransactionType.BUY,"TSLA",10);

        assertTrue(stocksFollowed.containsStock("GOOGL"));
        assertTrue(stocksFollowed.containsStock("F"));

        //Buy shares that account follows, but is not invested in
        assertFalse(investmentCollection.isInvested("GOOGL"));
        assertFalse(investmentCollection.isInvested("F"));

        conn.trade(TransactionType.BUY, "GOOGL",10);
        conn.trade(TransactionType.BUY, "F",15);

        //Check that each of these are caught &
        //for the right reasons
        try {
            conn.getModel().setWalletBalance(new BigDecimal(0.00));
            conn.trade(TransactionType.BUY, "AMZN", 12);
            conn.trade(TransactionType.BUY, "TSLA", 10);

        }catch(TradeException e) {
            logger.warn(e.getMessage());
        }
        try{
            conn.trade(TransactionType.BUY, "AMZN", -1);
        }catch (TradeException e ) {
            logger.warn(e.getMessage());
        }
        try{
            conn.getModel().setWalletBalance(new BigDecimal(100000));
            conn.getModel().update();
            conn.trade(TransactionType.BUY,"BDX",12);
        }catch(TradeException ex){
            logger.warn(ex.getMessage());
        }


        stocksFollowed.updateStocksFollowed(conn.getModel().getFollowedStocks());
        investmentCollection.updateInvestments(conn.getModel().getInvestedStocks());
        transactionHistory.updateTransactions(conn.getModel().getTransactionHistory());

        //since we already had investments in these companies, their numshares should just be updated
        assertEquals(investmentCollection.getInvestment("AMZN").getNumShares(), 22);
        assertEquals(investmentCollection.getInvestment("TSLA").getNumShares(), 15);

        //since we were only following these companies, the # of shares should be equal to what we bought in this test
        assertEquals(investmentCollection.getInvestment("GOOGL").getNumShares(), 10);
        assertEquals(investmentCollection.getInvestment("F").getNumShares(), 15);
        System.out.println( "Debug here: " + conn.getModel().getFollowedStocks());

        //For these shares, they should have been removed from following and placed in investment
        assertFalse(conn.getModel().getFollowedStocks().contains("GOOGL"));
        assertFalse(stocksFollowed.containsStock("F"));
        //and data that was unchanged during test should remain


        //The investment list should have expanded by 2 and not four, because only two investments were made in companies that
        assertEquals(investmentCollection.getInvestments().size(), init_size+2);

        //verify that transaction was expanded for every transaction made in this test
        assertEquals(transactionHistory.getTransactions().size(), initial_num_transactions+4);


    }

    //test just the sell portion of the trade function
    @Test
    public void testSell() throws SQLException {
        TransactionHistory transactionHistory = new TransactionHistory(conn.getModel().getTransactionHistory());
        InvestmentCollection investmentCollection = new InvestmentCollection(conn.getModel().getInvestedStocks());
        StocksFollowed stocksFollowedSell = new StocksFollowed(conn.getModel().getFollowedStocks());
        int init_size = investmentCollection.getInvestments().size();
        int initial_num_transactions = transactionHistory.getTransactions().size();

        //sell shares that account already has shares in
        assertTrue(investmentCollection.isInvested("AMZN"));

        assertTrue(investmentCollection.isInvested("TSLA"));
        assertFalse(stocksFollowedSell.containsStock("TSLA"));

        conn.trade(TransactionType.SELL, "AMZN",10);

        conn.trade(TransactionType.SELL, "TSLA",5);


        conn.getModel().update();
        investmentCollection.updateInvestments(conn.getModel().getInvestedStocks());
        System.out.println("Stocks folllowed: " + conn.getModel().getFollowedStocks());

        stocksFollowedSell.updateStocksFollowed(conn.getModel().getFollowedStocks());


        assertEquals(2, investmentCollection.getInvestment("AMZN").getNumShares());


        //since we sold all shares of tesla, tesla should no longer be in the invested list
        //and should be moved back to followed
        assertFalse(investmentCollection.isInvested("TSLA"));
        assertTrue(stocksFollowedSell.containsStock("TSLA"));

    }

    @Test
    public void testFollowStock() throws SQLException {
        StocksFollowed tempFollow = new StocksFollowed(conn.getModel().getFollowedStocks());
        assertTrue(tempFollow.containsStock("GOOGL"));
        assertTrue(tempFollow.containsStock("F"));

        assertFalse(tempFollow.containsStock("TSLA"));
        assertFalse(tempFollow.containsStock("AMZN"));
        assertFalse(tempFollow.containsStock("BDX"));

        conn.followStock("TSLA");
        conn.followStock("AMZN");
        conn.followStock("BDX");

        tempFollow.updateStocksFollowed(conn.getModel().getFollowedStocks());
        conn.getModel().update();

        assertTrue(tempFollow.containsStock("TSLA"));
        assertTrue(tempFollow.containsStock("AMZN"));
        assertTrue(tempFollow.containsStock("BDX"));
    }








}
