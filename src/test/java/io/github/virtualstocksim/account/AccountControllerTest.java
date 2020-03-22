package io.github.virtualstocksim.account;

import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionHistory;
import org.junit.Before;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class AccountControllerTest
{
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

       // List <Follow>followList = new LinkedList<Follow>();
       // followList.add(new Follow(new BigDecimal(100), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA)));
       // stocksFollowed = new StocksFollowed(followList);

        //transactions = new LinkedList<Transaction>();
        //transactionHistory = new TransactionHistory(transactions);

        //transactions.add(new Transaction(TransactionType.BUY,"3/18/2020",new BigDecimal("1800.00"),5, Amazon));

        acc= new Account(0, uuid, "ADMIN", "VSSAdmin@vss.com", "VSSAdmin", hash, salt,
                "", "",-1,"Fun text","my-picture.jpg", Timestamp.valueOf(Instant.now().toString()));

        conn.setModel(acc);
    }

    // No tests yet because account controller has no methods



}
