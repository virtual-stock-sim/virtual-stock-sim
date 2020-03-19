package io.github.virtualstocksim.transactionHistory;

import io.github.virtualstocksim.database.DatabaseConnections;
import io.github.virtualstocksim.stock.DummyStocks;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionType;
import org.junit.ClassRule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TransactionTest extends DatabaseConnections
{
    @ClassRule
    public static DatabaseConnections databases = new DatabaseConnections();

   private LinkedList<Transaction> transactions = new LinkedList<>();
    public void setUp() {
        transactions.add(new Transaction(TransactionType.BUY, "3/13/20", new BigDecimal(1252.2), 1, Stock.Find(1).get()));
        transactions.add(new Transaction(TransactionType.BUY, "5/8/77", new BigDecimal(50.12), 7, Stock.Find(2).get()));
        transactions.add(new Transaction(TransactionType.SELL, "5/18/18", new BigDecimal(500.7), 8, Stock.Find(3).get()));
        transactions.add(new Transaction(TransactionType.BUY, "3/13/20", new BigDecimal(123.8), 100, Stock.Find(4).get()));
        transactions.add(new Transaction(TransactionType.SELL, "3/13/20", new BigDecimal(65.2), 12, Stock.Find(5).get()));
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
        transactions.get(0).setType(TransactionType.SELL);
        transactions.get(1).setType(TransactionType.SELL);
        transactions.get(2).setType(TransactionType.BUY);
        transactions.get(3).setType(TransactionType.SELL);
        transactions.get(4).setType(TransactionType.BUY);

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
       assertEquals(transactions.get(0).getStock().getStockData(),Stock.Find(1).get().getStockData());
       assertEquals(transactions.get(1).getStock().getStockData(),Stock.Find(2).get().getStockData());
       assertEquals(transactions.get(2).getStock().getStockData(),Stock.Find(3).get().getStockData());
       assertEquals(transactions.get(3).getStock().getStockData(),Stock.Find(4).get().getStockData());
       assertEquals(transactions.get(4).getStock().getStockData(),Stock.Find(5).get().getStockData());

   }

    @Test
    public void testSetStock(){
        this.setUp();
        Stock amazon = DummyStocks.GetDummyStock(DummyStocks.StockSymbol.AMAZON);//new Stock (6, "BRK.A", new BigDecimal("289000.00"), 6);
        transactions.get(0).setStock(amazon);
        assertEquals(transactions.get(0).getStock(),amazon);


        Stock tesla = DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA);
        transactions.get(3).setStock(tesla);
        assertEquals(transactions.get(3).getStock(),tesla);

    }

}
