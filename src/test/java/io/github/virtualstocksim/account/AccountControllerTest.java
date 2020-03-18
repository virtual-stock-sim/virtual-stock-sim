package io.github.virtualstocksim.account;

import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionHistory;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class AccountControllerTest
{
    private AccountController conn;
    private Account acc;

    @Before
    public void setup(){
        conn = new AccountController();
        byte[] bytes = {3,4,5,6,7,8};
        LinkedList<Stock> stocksFollowed = new LinkedList<Stock>();
        List<Transaction> transactions = new LinkedList<Transaction>();
        TransactionHistory transactionHistory = new TransactionHistory(transactions);

        acc = new Account(0, "371298372189", AccountType.ADMIN, "VSSAdmin",bytes,bytes,
                stocksFollowed, transactionHistory,-1,"","");

        conn.setModel(acc);
    }

    // No tests yet because account controller has no methods


}
