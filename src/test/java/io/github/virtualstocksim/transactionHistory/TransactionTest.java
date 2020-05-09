package io.github.virtualstocksim.transactionHistory;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.stock.DummyStocks;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionHistory;
import io.github.virtualstocksim.transaction.TransactionType;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TransactionTest
{
   private LinkedList<Transaction> transactions = new LinkedList<>();

    public void setUp() {

        transactions.add(new Transaction(TransactionType.BUY,  Timestamp.valueOf("2020-03-23 04:46:05.123456"), new BigDecimal(1252.2), 1, DummyStocks.GetDummyStock(DummyStocks.StockSymbol.AMAZON)));
        transactions.add(new Transaction(TransactionType.BUY, Timestamp.valueOf("1977-05-08 01:15:30.123456"), new BigDecimal(50.12), 7, DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA)));
        transactions.add(new Transaction(TransactionType.SELL,  Timestamp.valueOf("2018-05-18 04:46:05.123456"), new BigDecimal(500.7), 8, DummyStocks.GetDummyStock(DummyStocks.StockSymbol.GOOGLE)));
        transactions.add(new Transaction(TransactionType.BUY,  Timestamp.valueOf("2016-03-23 04:46:05.123456"), new BigDecimal(123.8), 100, DummyStocks.GetDummyStock(DummyStocks.StockSymbol.FORD)));
        transactions.add(new Transaction(TransactionType.SELL, Timestamp.valueOf("2008-11-22 04:46:05.123456"), new BigDecimal(65.2), 12, DummyStocks.GetDummyStock(DummyStocks.StockSymbol.BDX)));
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
    public void testGetTimestamp(){
        this.setUp();
        assertEquals("2020-03-23 04:46:05.123456",transactions.get(0).getTimestamp().toString());
        assertEquals("1977-05-08 01:15:30.123456",transactions.get(1).getTimestamp().toString());
        assertEquals("2018-05-18 04:46:05.123456",transactions.get(2).getTimestamp().toString());
        assertEquals("2016-03-23 04:46:05.123456",transactions.get(3).getTimestamp().toString());
        assertEquals("2008-11-22 04:46:05.123456",transactions.get(4).getTimestamp().toString());
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
    public void testSetTimestamp(){
        this.setUp();
        transactions.get(0).setTimestamp(Timestamp.valueOf("1212-12-12 12:12:34.123456"));
        assertEquals("1212-12-12 12:12:34.123456",transactions.get(0).getTimestamp().toString());

        transactions.get(1).setTimestamp(Timestamp.valueOf("2020-04-24 01:26:48.123456"));
        assertEquals("2020-04-24 01:26:48.123456",transactions.get(1).getTimestamp().toString());

        Timestamp curTime = (SQL.GetTimeStamp());
        transactions.get(3).setTimestamp(curTime);
        assertEquals(curTime.toString(),transactions.get(3).getTimestamp().toString());
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
       assertEquals(transactions.get(0).getStock(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.AMAZON));
       assertEquals(transactions.get(1).getStock(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA));
       assertEquals(transactions.get(2).getStock(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.GOOGLE));
       assertEquals(transactions.get(3).getStock(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.FORD));
       assertEquals(transactions.get(4).getStock(),DummyStocks.GetDummyStock(DummyStocks.StockSymbol.BDX));

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
