package io.github.virtualstocksim.account;

import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionHistory;
import io.github.virtualstocksim.transaction.TransactionType;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class AccountTest
{
    private Account account;
    StocksFollowed stocksFollowed;
    Stock Amazon;
    List<Transaction> transactions;
    TransactionHistory transactionHistory;
    List<Stock> stocks;
    String uuid;
    byte[] hash;
    byte[] salt;

    @Before
    public void setup() {
        Encryption encrypt = new Encryption();
        String password = "virtualstocksim";
        salt = encrypt.getNextSalt();
        hash = encrypt.hash(password.toCharArray(),salt);
        System.out.println(hash);

        uuid = UUID.randomUUID().toString();
       stocks = new LinkedList<Stock>();
       stocksFollowed = new StocksFollowed(stocks);
       Amazon = new Stock(0, "AMZN",new BigDecimal("1800.00"),1);
       stocksFollowed.addStock(Amazon);

       transactions = new LinkedList<Transaction>();
       transactionHistory = new TransactionHistory(transactions);

       transactions.add(new Transaction(TransactionType.BUY,"3/18/2020",new BigDecimal("1800.00"),5, Amazon));

     account = new Account(0, uuid, AccountType.ADMIN, "VSSAdmin@vss.com",
             "VSSAdmin", hash, salt, stocksFollowed, transactionHistory,-1,"Fun text","my-picture.jpg");
     account.setPword("virtualstocksim");

    }

    @Test
    public void testGetUsername() {
        assertEquals("VSSAdmin", account.getUname());
    }

    @Test
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
        assertEquals(AccountType.ADMIN, account.getAccountType());
    }

    @Test
    public void testGetUUID(){
        assertEquals(uuid, account.getUUID());
    }

    @Test
    public void testGetStocksFollowed() {
        assertEquals(stocksFollowed, account.getStocksFollowed() );
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
        assertEquals(transactionHistory, account.getTransactionHistory());
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

    @Test
    public void testSetUUID(){
        String test_uuid = UUID.randomUUID().toString();
        account.setUuid(test_uuid);
        assertEquals(test_uuid, account.getUUID());
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
}
