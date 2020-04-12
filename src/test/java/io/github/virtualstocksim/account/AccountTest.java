package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.stock.DummyStocks;
import io.github.virtualstocksim.stock.Stock;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

public class AccountTest
{
    private static final Logger logger = LoggerFactory.getLogger(AccountTest.class);
    private Account account;
   // StocksFollowed stocksFollowed;
    Stock Amazon;
   // List<Transaction> transactions;
   // TransactionHistory transactionHistory;
    List<Stock> stocks;
    String uuid;
    byte[] hash;
    byte[] salt;

    private static final String TEST_PASSWORD = "virtualstocksim";

    @Before
    public void setup() {
        // initialization of necessary instances
        Encryption encrypt = new Encryption();
        String password = "virtualstocksim";
        salt = encrypt.getNextSalt();
        hash = encrypt.hash(password.toCharArray(),salt);
        uuid = UUID.randomUUID().toString();

        //List <Follow>followList = new LinkedList<Follow>();
       // followList.add(new Follow(new BigDecimal(100), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA)));
        //stocksFollowed = new StocksFollowed(followList);

        Amazon = DummyStocks.GetDummyStock(DummyStocks.StockSymbol.AMAZON);

        //transactions = new LinkedList<Transaction>();
        //transactionHistory = new TransactionHistory(transactions);
       // transactions.add(new Transaction(TransactionType.BUY,"3/18/2020",new BigDecimal("1800.00"),5, Amazon));

        // create and populate account with objects
     account = new Account(0, uuid, AccountType.ADMIN, "VSSAdmin@vss.com",
                           "VSSAdmin", hash, salt, "", "", "", new BigDecimal("0.0"),  -1, "Fun text",
                           "my-picture.jpg", SQL.GetTimeStamp());

    }

    @Test
    public void testGetUsername() {
        assertEquals("VSSAdmin", account.getUsername());
    }

/*    @Test
    public void testGetPassword() {
        assertEquals("virtualstocksim", account.getPword());
    }

    @Test
    public void testGetEmail() {
        account.setEmail("dpalmieri@ycp.edu");
        assertEquals("dpalmieri@ycp.edu", account.getEmail());
    }

    @Test
    public void testGetID() {
        assertEquals(0,account.getId());
    }

    @Test
    public void testGetAccountType(){
        assertEquals(AccountType.ADMIN, account.getType());
    }

    @Test
    public void testGetUUID(){
        assertEquals(uuid, account.getUUID());
    }

    @Test
    public void testGetStocksFollowed() {
        assertEquals("", account.getFollowedStocks());
    }

    @Test
    public void testGetPasswordHash(){
        assertEquals(hash, account.getPasswordHash());
    }

    @Test
    public void testGetPasswordSalt(){
        assertEquals(salt, account.getPasswordSalt());
    }

    @Test
    public void testGetTransactionHistory(){
        assertEquals("", account.getTransactionHistory());
    }

    @Test
    public void testGetLeaderboardRank() {
        assertEquals(-1, account.getLeaderboardRank());
    }

    @Test
    public void testGetBio(){
        assertEquals("Fun text", account.getBio());
    }

    @Test
    public void testGetProfilePicture(){
        assertEquals("my-picture.jpg", account.getProfilePicture());
    }

/*    @Test
    public void testSetUUID(){
        String test_uuid = UUID.randomUUID().toString();
        account.set(test_uuid);
        assertEquals(test_uuid, account.getUUID());
    }

    @Test
    public void testGetInvestedStocks(){
        account.setInvestedStocks("Amazon,Tesla");
        assertEquals(account.getInvestedStocks(),"Amazon,Tesla");
    }

    @Test
    public void testGetWalletBalance(){
        account.setWalletBalance(new BigDecimal("10000.00"));
        assertEquals(account.getWalletBalance(), new BigDecimal("10000.00"));
    }

    @Test
    public void testSetPasswordHash(){
        account.setPasswordHash(hash);
        assertEquals(hash, account.getPasswordHash());
    }

    @Test
    public void testSetPasswordSalt(){
        account.setPasswordSalt(salt);
        assertEquals(salt, account.getPasswordSalt());
    }

    @Test
    public void testSetLeaderboardRank(){
        account.setLeaderboardRank(4);
        assertEquals(4, account.getLeaderboardRank());
    }

    @Test
    public void testSetBio(){
        account.setBio("Tesla is the best car company in the world");
        assertEquals("Tesla is the best car company in the world", account.getBio());
    }

    @Test
    public void testSetProfilePicture(){
        account.setProfilePicture("new-picture.jpg");
        assertEquals("new-picture.jpg", account.getProfilePicture());
    }

    @Test
    public void testSetFollowedStocks(){
        account.setFollowedStocks("Amazon, BD");
        assertEquals(account.getFollowedStocks(), "Amazon, BD");
    }

    @Test
    public void testSetTransactionHistory(){
        account.setTransactionHistory("AMZN 4/11 1 $300");
        assertEquals(account.getTransactionHistory(),"AMZN 4/11 1 $300");
    }

    @Test
    public void testCreateAccountInDB(){
        try(Connection conn = AccountDatabase.getConnection())
        {
            SQL.executeUpdate(conn, "DELETE FROM accounts WHERE username = ? ", "DanPalm5");
        }
        catch (SQLException e)
        {
            fail();
        }

        Optional<Account> new_acc =  Account.Create("DanPalm5", "test@test.com","topsecret",AccountType.ADMIN);
        if(!new_acc.isPresent()){ fail(); }
        Optional<Account> find_acc = Account.Find("DanPalm5");
        if(!find_acc.isPresent()){ fail(); }
        assertEquals(new_acc.get().getId(), find_acc.get().getId());
        assertEquals(new_acc.get().getUUID(), find_acc.get().getUUID());
        assertEquals(new_acc.get().getUsername(),find_acc.get().getUsername());
        assertEquals(new_acc.get().getEmail(), find_acc.get().getEmail());
        assertArrayEquals(new_acc.get().getPasswordHash(), find_acc.get().getPasswordHash());
        assertArrayEquals(new_acc.get().getPasswordSalt(), find_acc.get().getPasswordSalt());
        //TODO: Add these tests back when this feature is functional
        //assertEquals(new_acc.get().getStocksFollowed(),find_acc.get().getStocksFollowed());
        //assertEquals(new_acc.get().getTransactionHistory(),find_acc.get().getTransactionHistory());
        assertEquals(new_acc.get().getLeaderboardRank(), find_acc.get().getLeaderboardRank());
        assertEquals(new_acc.get().getBio(), find_acc.get().getBio());
        assertEquals(new_acc.get().getProfilePicture(), find_acc.get().getProfilePicture());
        assertEquals(new_acc.get().getCreationDate(), find_acc.get().getCreationDate());

    }

}
