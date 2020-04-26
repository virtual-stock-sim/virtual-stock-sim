package io.github.virtualstocksim.investment;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.transaction.Investment;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvestmentTest {

    List<Investment> investmentList = new LinkedList<>();
    @Before
    public void setUp() {
        investmentList.add(new Investment(1, "AMZN", SQL.GetTimeStamp()));
        investmentList.add(new Investment(100, "TSLA", SQL.GetTimeStamp()));
        investmentList.add(new Investment(3, "GOOGL", SQL.GetTimeStamp()));
        investmentList.add(new Investment(7, "F", SQL.GetTimeStamp()));
        investmentList.add(new Investment(9, "BDX", SQL.GetTimeStamp()));
    }



    @Test
    public void testGetTotalHoldings(){
        assertEquals(investmentList.get(0).getTotalHoldings().doubleValue(),100, 0.00001);
        assertEquals(investmentList.get(1).getTotalHoldings().doubleValue(),20000, 0.00001);
        assertEquals(investmentList.get(2).getTotalHoldings().doubleValue(),900, 0.00001);
        assertEquals(investmentList.get(3).getTotalHoldings().doubleValue(),2800, 0.00001);
        assertEquals(investmentList.get(4).getTotalHoldings().doubleValue(),4500, 0.00001);

    }

    @Test
    public void testGetNumShares(){
        assertEquals(investmentList.get(0).getNumShares(),1);
        assertEquals(investmentList.get(1).getNumShares(),100);
        assertEquals(investmentList.get(2).getNumShares(),3);
        assertEquals(investmentList.get(3).getNumShares(),7);
        assertEquals(investmentList.get(4).getNumShares(),9);
    }

    @Test
    public void testSetNumShares(){

        assertEquals(investmentList.get(0).getNumShares(),1);
        investmentList.get(0).setNumShares(32);
        assertEquals(investmentList.get(0).getNumShares(),32);

        assertEquals(investmentList.get(1).getNumShares(),100);
        investmentList.get(1).setNumShares(30000000);
        assertEquals(investmentList.get(1).getNumShares(),30000000);

        assertEquals(investmentList.get(2).getNumShares(),3);
        investmentList.get(2).setNumShares(0);
        assertEquals(investmentList.get(2).getNumShares(),0);

        //check that the error is caught and numShares does not change
        assertEquals(investmentList.get(3).getNumShares(),7);
        investmentList.get(3).setNumShares(-3);
        assertEquals(investmentList.get(3).getNumShares(),7);

    }

    @Test
    public void testGetSymbol(){
        assertTrue(investmentList.get(0).getSymbol().equals("AMZN"));
        assertTrue(investmentList.get(1).getSymbol().equals("TSLA"));
        assertTrue(investmentList.get(2).getSymbol().equals("GOOGL"));
        assertTrue(investmentList.get(3).getSymbol().equals("F"));
        assertTrue(investmentList.get(4).getSymbol().equals("BDX"));

    }
}
