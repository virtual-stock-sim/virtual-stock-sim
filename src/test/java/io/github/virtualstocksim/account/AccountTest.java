package io.github.virtualstocksim.account;

import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionHistory;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class AccountTest
{
    private Account account;

    @Before
    public void setup() {
        byte[] bytes = {3,4,5,6,7,8};
        LinkedList<Stock> stocksFollowed = new LinkedList<Stock>();
        List<Transaction> transactions = new LinkedList<Transaction>();
        TransactionHistory transactionHistory = new TransactionHistory(transactions);

     account = new Account(0, "371298372189", AccountType.ADMIN, "VSSAdmin",bytes,bytes,
                stocksFollowed, transactionHistory,-1,"","");

    }

    @Test
    public void testGetUsername() {
        account.setUname("Dan");
        assertEquals("Dan", account.getUname());
    }

    @Test
    public void testGetPassword() {
        account.setPword("VSS");
        assertEquals("VSS", account.getPword());
    }

    @Test
    public void testGetEmail() {
        account.setEmail("dpalmieri@ycp.edu");
        assertEquals("dpalmieri@ycp.edu", account.getEmail());
    }
}
