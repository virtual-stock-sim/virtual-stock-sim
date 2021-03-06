package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.following.FollowedStock;
import io.github.virtualstocksim.following.FollowedStocks;
import io.github.virtualstocksim.stock.DummyStocks;
import io.github.virtualstocksim.stock.DummyStocks.StockSymbol;
import io.github.virtualstocksim.stock.ResetStockDB;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.Assert.*;

public class AccountControllerTest
{
    private static final Logger logger = LoggerFactory.getLogger(AccountControllerTest.class);
    private AccountController conn;
    private Account acct;
    FollowedStocks followedStocks;
    InvestmentCollection investmentCollection;
    Stock Amazon;
    List<Transaction> transactions;
    TransactionHistory transactionHistory;
    List<Stock> stocks;
    String uuid;

    private static final String TEST_PASSWORD = "virtualstocksim";

    @Before
    public void resetDB()
    {
        ResetStockDB.reset();
        ResetAccountDB.reset();
    }

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


        Map<String, FollowedStock> followedStockMap = new HashMap<>();
        //followList.add(new Follow(new BigDecimal(200), Stock.Find("AMZN").get(),SQL.GetTimeStamp()));
        //followList.add(new Follow(new BigDecimal(600), Stock.Find("TSLA").get(),SQL.GetTimeStamp()));
        followedStockMap.put(StockSymbol.GOOGLE.getSymbol(), new FollowedStock(DummyStocks.GetDummyStock(StockSymbol.GOOGLE), new BigDecimal(23), SQL.GetTimeStamp()));
        followedStockMap.put(StockSymbol.FORD.getSymbol(), new FollowedStock(DummyStocks.GetDummyStock(StockSymbol.FORD), new BigDecimal(12), SQL.GetTimeStamp()));
       // followList.add(new Follow(new BigDecimal(100), Stock.Find("BDX").get(),SQL.GetTimeStamp())); //add later to test addInvestment
        followedStocks = new FollowedStocks(followedStockMap);
        conn.getModel().setFollowedStocks(String.valueOf(followedStocks.asJsonArray()));

        List investmentList = new LinkedList<>();
        investmentList.add(new Investment(12,Stock.Find("AMZN").orElse(null),SQL.GetTimeStamp()));
        investmentList.add(new Investment(5, Stock.Find("TSLA").orElse(null),SQL.GetTimeStamp()));
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
        assertTrue((conn.login(conn.getModel().getUsername(), TEST_PASSWORD)));

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
        //System.out.println("Parsing to investmentCollection" + conn.getModel().getInvestedStocks());
        FollowedStocks followedStocks = new FollowedStocks(conn.getModel().getFollowedStocks());

        conn.setModel(conn.getModel());

        int init_size = investmentCollection.getInvestments().size();
        int initial_num_transactions = transactionHistory.getTransactions().size();

        //buy shares that account already has shares in
        assertTrue(investmentCollection.isInvested("AMZN"));
        assertEquals(investmentCollection.getInvestment("AMZN").getNumShares(), 12);

        assertTrue(investmentCollection.isInvested("TSLA"));
        assertEquals(investmentCollection.getInvestment("TSLA").getNumShares(),5);

        conn.trade(TransactionType.BUY, "AMZN", 10);
        conn.trade(TransactionType.BUY,"TSLA",10);

        assertTrue(followedStocks.contains("GOOGL"));
        assertTrue(followedStocks.contains("F"));

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


        followedStocks.setFollowedStocks(conn.getModel().getFollowedStocks());
        investmentCollection.updateInvestments(conn.getModel().getInvestedStocks());
        transactionHistory.updateTransactions(conn.getModel().getTransactionHistory());

        //since we already had investments in these companies, their numshares should just be updated
        assertEquals(investmentCollection.getInvestment("AMZN").getNumShares(), 22);
        assertEquals(investmentCollection.getInvestment("TSLA").getNumShares(), 15);

        //since we were only following these companies, the # of shares should be equal to what we bought in this test
        assertEquals(investmentCollection.getInvestment("GOOGL").getNumShares(), 10);
        assertEquals(investmentCollection.getInvestment("F").getNumShares(), 15);
        //System.out.println( "Debug here: " + conn.getModel().getFollowedStocks());

        //For these shares, they should have been removed from following and placed in investment
        assertFalse(conn.getModel().getFollowedStocks().contains("GOOGL"));
        assertFalse(followedStocks.contains("F"));
        //and data that was unchanged during test should remain


        //The investment list should have expanded by 2 and not four, because only two investments were made in companies that
        assertEquals(init_size+2, investmentCollection.getInvestments().size());

        //verify that transaction was expanded for every transaction made in this test
        assertEquals(transactionHistory.getTransactions().size(), initial_num_transactions+4);


    }

    //test just the sell portion of the trade function
    @Test
    public void testSell() throws SQLException, TradeException {
        TransactionHistory transactionHistory = new TransactionHistory(conn.getModel().getTransactionHistory());
        InvestmentCollection investmentCollection = new InvestmentCollection(conn.getModel().getInvestedStocks());
        FollowedStocks followedStocksSell = new FollowedStocks(conn.getModel().getFollowedStocks());
        conn.setModel(conn.getModel());
        int init_size = investmentCollection.getInvestments().size();
        int initial_num_transactions = transactionHistory.getTransactions().size();

        //sell shares that account already has shares in
        assertTrue(investmentCollection.isInvested("AMZN"));

        assertTrue(investmentCollection.isInvested("TSLA"));
        assertFalse(followedStocksSell.contains("TSLA"));

        conn.trade(TransactionType.SELL, "AMZN",10);

        conn.trade(TransactionType.SELL, "TSLA",5);


        conn.getModel().update();
        investmentCollection.updateInvestments(conn.getModel().getInvestedStocks());
       // System.out.println(conn.getModel().getInvestedStocks());
        //System.out.println("Stocks folllowed: " + conn.getModel().getFollowedStocks());

        followedStocksSell.setFollowedStocks(conn.getModel().getFollowedStocks());


        assertEquals(2, investmentCollection.getInvestment("AMZN").getNumShares());


        //since we sold all shares of tesla, tesla should no longer be in the invested list
        //and should be moved back to followed
        assertFalse(investmentCollection.isInvested("TSLA"));
        assertTrue(followedStocksSell.contains("TSLA"));

    }

    @Test
    public void testFollowStock() throws SQLException {
        FollowedStocks tempFollow = new FollowedStocks(conn.getModel().getFollowedStocks());
        assertTrue(tempFollow.contains("GOOGL"));
        assertTrue(tempFollow.contains("F"));

        assertFalse(tempFollow.contains("TSLA"));
        assertFalse(tempFollow.contains("AMZN"));
        assertFalse(tempFollow.contains("BDX"));

        conn.followStock(DummyStocks.GetDummyStock(StockSymbol.TESLA));
        conn.followStock(DummyStocks.GetDummyStock(StockSymbol.AMAZON));
        conn.followStock(DummyStocks.GetDummyStock(StockSymbol.BDX));

        tempFollow.setFollowedStocks(conn.getModel().getFollowedStocks());
        conn.getModel().update();

        assertTrue(tempFollow.contains("TSLA"));
        assertTrue(tempFollow.contains("AMZN"));
        assertTrue(tempFollow.contains("BDX"));
    }

    @Test
    public void testResetTransactionHistory() {
        conn.resetTransactionHistory();
        assertTrue(conn.getModel().getTransactionHistory().trim().isEmpty());
    }

    @Test
    public void testResetFollowing() {
        conn.resetFollowed();
        assertTrue(conn.getModel().getFollowedStocks().trim().isEmpty());
    }

    @Test
    public void testOptIn(){
        conn.optInToLeaderboard();
        assertNotEquals(conn.getModel().getLeaderboardRank(), -1);
    }

    @Test
    public void testOptOut(){
        conn.optOutOfLeaderboard();
        assertEquals(conn.getModel().getLeaderboardRank(), -1);
    }

    @Test
    public void testResetEmail() {
        conn.resetEmail("admin@vss.com");
        assertEquals(conn.getModel().getEmail(), "admin@vss.com");
        assertFalse(conn.getModel().getEmail().equals("test@vss.com"));
    }

    @Test
    public void testUnInvest() throws SQLException, TradeException {
        conn.unInvest("AMZN");
        InvestmentCollection investmentCollection = new InvestmentCollection(conn.getModel().getInvestedStocks());
        assertFalse(investmentCollection.isInvested("AMZN"));
        assertTrue(investmentCollection.isInvested("TSLA"));

    }








}
