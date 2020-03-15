package io.github.virtualstocksim.transactionHistory;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.stock.Stock;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Optional;

import static org.junit.Assert.*;

public class TransactionTest {
   private LinkedList<Transaction> transactions = new LinkedList<>();
    public void setUp() {
        transactions.add(new Transaction(Transaction.TransactionType.BUY, "3/13/20", new BigDecimal(1252.2), 1, Stock.GetStock(1).get()));
        transactions.add(new Transaction(Transaction.TransactionType.BUY, "5/8/77", new BigDecimal(50.12), 7, Stock.GetStock(2).get()));
        transactions.add(new Transaction(Transaction.TransactionType.SELL, "5/18/18", new BigDecimal(500.7), 8, Stock.GetStock(3).get()));
        transactions.add(new Transaction(Transaction.TransactionType.BUY, "3/13/20", new BigDecimal(123.8), 100, Stock.GetStock(4).get()));
        transactions.add(new Transaction(Transaction.TransactionType.SELL, "3/13/20", new BigDecimal(65.2), 12, Stock.GetStock(5).get()));
    }


    @Test
    public void testGetType(){
    this.setUp();
        assertEquals("BUY",transactions.get(0).getType().toString());
        assertEquals("BUY",transactions.get(1).getType().toString());
        assertEquals("SELL",transactions.get(2).getType().toString());
        assertEquals("BUY",transactions.get(3).getType().toString());
        assertEquals("SELL",transactions.get(4).getType().toString());
    }

    @Test
    public void testGetDate(){
        this.setUp();
        assertEquals("3/13/20",transactions.get(0).getDate());
        assertEquals("5/8/77",transactions.get(1).getDate());
        assertEquals("5/18/18",transactions.get(2).getDate());
        assertEquals("3/13/20",transactions.get(3).getDate());
        assertEquals("3/13/20",transactions.get(4).getDate());
    }

    @Test
    public void testGetNumShares(){
        this.setUp();
        assertEquals(1,transactions.get(0).getNumShares());
        assertEquals(7,transactions.get(1).getNumShares());
        assertEquals(8,transactions.get(2).getNumShares());
        assertEquals(100,transactions.get(3).getNumShares());
        assertEquals(12,transactions.get(4).getNumShares());

    }

    @Test
    public void testGetPricePerShare(){
        this.setUp();
        assertEquals(new BigDecimal(1252.2),transactions.get(0).getPricePerShare());
        assertEquals(new BigDecimal(50.12),transactions.get(1).getPricePerShare());
        assertEquals(new BigDecimal(500.7),transactions.get(2).getPricePerShare());
        assertEquals(new BigDecimal(123.8),transactions.get(3).getPricePerShare());
        assertEquals(new BigDecimal(65.2),transactions.get(4).getPricePerShare());
}

    @Test
    public void testGetVolumePrice(){
    this.setUp();
    assertEquals(new BigDecimal(1252.2).doubleValue(),transactions.get(0).getVolumePrice().doubleValue(),0.000001);
    assertEquals(new BigDecimal(350.84).doubleValue(),transactions.get(1).getVolumePrice().doubleValue(),0.00001);
    assertEquals(new BigDecimal(4005.6).doubleValue(),transactions.get(2).getVolumePrice().doubleValue(),0.00001);
    assertEquals(new BigDecimal(12380 ).doubleValue(),transactions.get(3).getVolumePrice().doubleValue(),0.00001);
    assertEquals(new BigDecimal(782.4 ).doubleValue(),transactions.get(4).getVolumePrice().doubleValue(),0.00001);

    }

    @Test
    public void testSetType(){
        this.setUp();
        //flip types from initial setup
        transactions.get(0).setType(Transaction.TransactionType.SELL);
        transactions.get(1).setType(Transaction.TransactionType.SELL);
        transactions.get(2).setType(Transaction.TransactionType.BUY);
        transactions.get(3).setType(Transaction.TransactionType.SELL);
        transactions.get(4).setType(Transaction.TransactionType.BUY);

        //verify they all flipped right
        assertEquals("SELL",transactions.get(0).getType().toString());
        assertEquals("SELL",transactions.get(1).getType().toString());
        assertEquals("BUY",transactions.get(2).getType().toString());
        assertEquals("SELL",transactions.get(3).getType().toString());
        assertEquals("BUY",transactions.get(4).getType().toString());
    }

    @Test
    public void testSetDate(){
        this.setUp();
        transactions.get(0).setDate("HI :)");
        assertEquals("HI :)",transactions.get(0).getDate());

        transactions.get(3).setDate("3/14/20");
        assertEquals("3/14/20",transactions.get(3).getDate());
    }


    @Test
    public void testSetNumShares(){
        this.setUp();
        assertNotEquals(transactions.get(1).getNumShares(),2);
       transactions.get(1).setNumShares(2);
        assertEquals(transactions.get(1).getNumShares(),2);

        assertNotEquals(transactions.get(2).getNumShares(),7);
        transactions.get(2).setNumShares(7);
        assertEquals(transactions.get(2).getNumShares(),7);

        assertNotEquals(transactions.get(3).getNumShares(),9);
        transactions.get(3).setNumShares(9);
        assertEquals(transactions.get(3).getNumShares(),9);

    }

    @Test
    public void testSetPricePerShare(){
        this.setUp();
        transactions.get(0).setPricePerShare(new BigDecimal(0));
        transactions.get(4).setPricePerShare(new BigDecimal(123.456));

        assertEquals(transactions.get(0).getPricePerShare(),new BigDecimal(0));
        transactions.get(4).setPricePerShare(new BigDecimal(123.456));

    }
   @Test
   public void testGetStock(){
        this.setUp();
       assertEquals(transactions.get(0).getStock().getStockData(),Stock.GetStock(1).get().getStockData());
       assertEquals(transactions.get(1).getStock().getStockData(),Stock.GetStock(2).get().getStockData());
       assertEquals(transactions.get(2).getStock().getStockData(),Stock.GetStock(3).get().getStockData());
       assertEquals(transactions.get(3).getStock().getStockData(),Stock.GetStock(4).get().getStockData());
       assertEquals(transactions.get(4).getStock().getStockData(),Stock.GetStock(5).get().getStockData());

   }

    @Test
    public void testSetStock(){
        this.setUp();
        Stock berkshire = new Stock (6, "BRK.A", new BigDecimal("289000.00"), 6);
        transactions.get(0).setStock(berkshire);
        assertEquals(transactions.get(0).getStock(),berkshire);


        Stock brett = new Stock (7, "BRETT", new BigDecimal("4600"), 7);
        transactions.get(3).setStock(brett);
        assertEquals(transactions.get(3).getStock(),brett);

    }

}
