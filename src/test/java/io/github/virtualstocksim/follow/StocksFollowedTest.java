package io.github.virtualstocksim.follow;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.following.Follow;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.DummyStocks;
import io.github.virtualstocksim.stock.Stock;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class StocksFollowedTest {
    List<Follow> dummyFollows= new LinkedList<>();
    StocksFollowed control;
    StocksFollowed test;
    @Before
    public void populate(){
        dummyFollows.add(new Follow(new BigDecimal(100), Stock.Find("AMZN").get(), SQL.GetTimeStamp()));
        dummyFollows.add(new Follow(new BigDecimal(498), Stock.Find("TSLA").get(), SQL.GetTimeStamp()));
        dummyFollows.add(new Follow(new BigDecimal(220), Stock.Find("GOOGL").get(), SQL.GetTimeStamp()));
        dummyFollows.add(new Follow(new BigDecimal(501), Stock.Find("F").get(), SQL.GetTimeStamp()));
        dummyFollows.add((new Follow(new BigDecimal(.12), Stock.Find("BDX").get(), SQL.GetTimeStamp())));
        control = new StocksFollowed(dummyFollows);
        //Control uses List constructor, test uses String constructor
        test= new StocksFollowed(control.followObjectsToSting());
    }

    //check that the List constructor works so that we know the later serialization test results are more accurate (negate errors from other possible sources like this)
    @Test
    public void testConstructors(){

        assertTrue(control.getStocksFollowed().get(0).getStock().getSymbol().equals("AMZN") && test.getStocksFollowed().get(0).getStock().getSymbol().equals("AMZN"));
        assertEquals(control.getStocksFollowed().get(0).getInitialPrice().doubleValue() ,100, 0.001);
        assertTrue(control.getStocksFollowed().get(1).getStock().getSymbol().equals("TSLA") && test.getStocksFollowed().get(1).getStock().getSymbol().equals("TSLA"));
        assertEquals(control.getStocksFollowed().get(1).getInitialPrice().doubleValue() ,498, 0.001);
        assertTrue(control.getStocksFollowed().get(2).getStock().getSymbol().equals("GOOGL")&& test.getStocksFollowed().get(2).getStock().getSymbol().equals("GOOGL"));
        assertEquals(control.getStocksFollowed().get(2).getInitialPrice().doubleValue() ,220, 0.001);
        assertTrue(control.getStocksFollowed().get(3).getStock().getSymbol().equals("F")&& test.getStocksFollowed().get(3).getStock().getSymbol().equals("F"));
        assertEquals(control.getStocksFollowed().get(3).getInitialPrice().doubleValue() ,501, 0.001);
        assertTrue(control.getStocksFollowed().get(4).getStock().getSymbol().equals("BDX")&& test.getStocksFollowed().get(4).getStock().getSymbol().equals("BDX"));
        assertEquals(control.getStocksFollowed().get(4).getInitialPrice().doubleValue() ,.12, 0.001);
    }

    //because the string constructor calls the stringToFollow method, this tests gives coverage to that method
    //Coverage of followObjectsToString is also provided because in the @Before, the test stocksFollowed is created by calling that method on the control StocksFollowed
    //thus, we do not need individual tests of these methods.
    @Test
    public void testFollowObjectsToString(){

        assertTrue(test.getStocksFollowed().get(0).getStock().getSymbol().equals("AMZN"));
        assertEquals(test.getStocksFollowed().get(0).getInitialPrice().doubleValue() ,100, 0.001);
        assertTrue(test.getStocksFollowed().get(1).getStock().getSymbol().equals("TSLA"));
        assertEquals(test.getStocksFollowed().get(1).getInitialPrice().doubleValue() ,498, 0.001);
        assertTrue(test.getStocksFollowed().get(2).getStock().getSymbol().equals("GOOGL"));
        assertEquals(test.getStocksFollowed().get(2).getInitialPrice().doubleValue() ,220, 0.001);
        assertTrue(test.getStocksFollowed().get(3).getStock().getSymbol().equals("F"));
        assertEquals(test.getStocksFollowed().get(3).getInitialPrice().doubleValue() ,501, 0.001);
        assertTrue(test.getStocksFollowed().get(4).getStock().getSymbol().equals("BDX"));
        assertEquals(test.getStocksFollowed().get(4).getInitialPrice().doubleValue() ,.12, 0.001);
    }

    @Test
    public void testContainsStock(){

        assertTrue(control.containsStock("AMZN"));
        assertTrue(control.containsStock("TSLA"));
        assertTrue(control.containsStock("GOOGL"));
        assertTrue(control.containsStock("F"));
        assertTrue(control.containsStock("BDX"));
        control.removeFollow("F");
        assertFalse(control.containsStock("F"));
        assertFalse(control.containsStock("BRETT"));
        assertFalse(control.containsStock("MSFT"));
    }

    @Test
    public void testRemoveFollow(){
        assertTrue(control.getStocksFollowed().size() == 5);
        assertTrue(control.containsStock("AMZN"));
        assertTrue(control.getStocksFollowed().get(0).getStock().getSymbol().equals("AMZN"));
        control.removeFollow("AMZN");
        assertTrue(control.getStocksFollowed().size() == 4);
        assertFalse(control.containsStock("AMZN"));

        assertTrue(control.containsStock("GOOGL"));
        assertTrue(control.getStocksFollowed().get(1).getStock().getSymbol().equals("GOOGL"));
        control.removeFollow("GOOGL");
        assertTrue(control.getStocksFollowed().size() == 3);
        assertFalse(control.containsStock("GOOGL"));


        assertTrue(control.containsStock("BDX"));
        assertTrue(control.getStocksFollowed().get(2).getStock().getSymbol().equals("BDX"));
        control.removeFollow("BDX");
        assertTrue(control.getStocksFollowed().size() == 2);
        assertFalse(control.containsStock("BDX"));

    }



}
