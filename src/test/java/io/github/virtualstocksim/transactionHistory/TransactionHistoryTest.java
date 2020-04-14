package io.github.virtualstocksim.transactionHistory;

import io.github.virtualstocksim.stock.DummyStocks;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionHistory;
import io.github.virtualstocksim.transaction.TransactionType;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TransactionHistoryTest {
    List<Transaction> transactions = new LinkedList<>();
    TransactionHistory control;
    TransactionHistory exp;
    @Before
    public void setUp() {
        transactions.add(new Transaction(TransactionType.BUY, Timestamp.valueOf("2020-03-23 04:46:05.123456"), new BigDecimal(1252.2), 1, Stock.Find("AMZN").get()));
        transactions.add(new Transaction(TransactionType.BUY, Timestamp.valueOf("1977-05-08 01:15:30.123456"), new BigDecimal(50.12), 7, Stock.Find("TSLA").get()));
        transactions.add(new Transaction(TransactionType.SELL, Timestamp.valueOf("2018-05-18 04:46:05.123456"), new BigDecimal(500.7), 8, Stock.Find("GOOGL").get()));
        transactions.add(new Transaction(TransactionType.BUY, Timestamp.valueOf("2016-03-23 04:46:05.123456"), new BigDecimal(123.8), 100, Stock.Find("F").get()));
        transactions.add(new Transaction(TransactionType.SELL, Timestamp.valueOf("2008-11-22 04:46:05.123456"), new BigDecimal(65.2), 12, Stock.Find("BDX").get()));
        control = new TransactionHistory(transactions);
        exp = new TransactionHistory(control.buildTransactionJSON());
    }

    //because the string constructor calls the parseTransactionFromJSON method, this tests gives coverage to that method as well
    //Coverage of buildTransactionJSON is also provided because in the @Before, the test TransactionHistory is created by using the String constructor (and therefore the string to list method as well)
    @Test
    public void testConstructors(){
        assertTrue(control.getTransactions().get(0).getStock().getSymbol().equals("AMZN"));
        assertTrue(control.getTransactions().get(1).getStock().getSymbol().equals("TSLA"));
        assertTrue(control.getTransactions().get(2).getStock().getSymbol().equals("GOOGL"));
        assertTrue(control.getTransactions().get(3).getStock().getSymbol().equals("F"));
        assertTrue(control.getTransactions().get(4).getStock().getSymbol().equals("BDX"));

        assertTrue(exp.getTransactions().get(0).getStock().getSymbol().equals("AMZN"));
        assertTrue(exp.getTransactions().get(1).getStock().getSymbol().equals("TSLA"));
        assertTrue(exp.getTransactions().get(2).getStock().getSymbol().equals("GOOGL"));
        assertTrue(exp.getTransactions().get(3).getStock().getSymbol().equals("F"));
        assertTrue(exp.getTransactions().get(4).getStock().getSymbol().equals("BDX"));
    }



}
