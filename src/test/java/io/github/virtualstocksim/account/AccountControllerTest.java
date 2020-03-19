package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.DatabaseConnections;
import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.following.Follow;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.DummyStocks;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionHistory;
import io.github.virtualstocksim.transaction.TransactionType;
import org.junit.Before;
import org.junit.ClassRule;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class AccountControllerTest
{
    @ClassRule
    public static DatabaseConnections databases = new DatabaseConnections();

    private AccountController conn;
    private Account acc;
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
        conn = new AccountController();
        Encryption encrypt = new Encryption();
        acc.setPword("virtualstocksim");
        salt = encrypt.getNextSalt();
        hash = encrypt.hash(acc.getPword().toCharArray(),salt);
        System.out.println(hash);

        uuid = UUID.randomUUID().toString();
        stocks = new LinkedList<Stock>();

        List <Follow>followList = new LinkedList<Follow>();
        followList.add(new Follow(new BigDecimal(100), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA)));
        stocksFollowed = new StocksFollowed(followList);

        transactions = new LinkedList<Transaction>();
        transactionHistory = new TransactionHistory(transactions);

        transactions.add(new Transaction(TransactionType.BUY,"3/18/2020",new BigDecimal("1800.00"),5, Amazon));

        acc= new Account(0, uuid, AccountType.ADMIN, "VSSAdmin@vss.com",
                "VSSAdmin", hash, salt, stocksFollowed, transactionHistory,-1,"Fun text","my-picture.jpg");

        conn.setModel(acc);
    }

    // No tests yet because account controller has no methods



}
