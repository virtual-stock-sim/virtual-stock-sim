package io.github.virtualstocksim.investment;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.transaction.Investment;
import io.github.virtualstocksim.transaction.InvestmentCollection;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class InvestmentCollectionTest {

    List<Investment> investmentList= new LinkedList<>();
    InvestmentCollection control;
    InvestmentCollection experimental;
    @Before
    public void setUp() {
        investmentList.add(new Investment(1, "AMZN", SQL.GetTimeStamp()));
        investmentList.add(new Investment(100, "TSLA", SQL.GetTimeStamp()));
        investmentList.add(new Investment(3, "GOOGL", SQL.GetTimeStamp()));
        investmentList.add(new Investment(7, "F", SQL.GetTimeStamp()));

        control = new InvestmentCollection(investmentList);
        experimental = new InvestmentCollection(control.buildJSON());
    }

    //provides to&from JSON methods by using both types of constructors on InvestmentCollection objects "control" & "experimental"
    @Test
    public void testConstructors(){

        assertTrue(control.getInvestments().get(0).getTicker().equals("AMZN"));
        assertTrue(control.getInvestments().get(0).getNumShares() == 1);

        assertTrue(control.getInvestments().get(1).getTicker().equals("TSLA"));
        assertTrue(control.getInvestments().get(1).getNumShares() == 100);

        assertTrue(control.getInvestments().get(2).getTicker().equals("GOOGL"));
        assertTrue(control.getInvestments().get(2).getNumShares() == 3);

        assertTrue(control.getInvestments().get(3).getTicker().equals("F"));
        assertTrue(control.getInvestments().get(3).getNumShares() == 7);




        assertTrue(experimental.getInvestments().get(0).getTicker().equals("AMZN"));
        assertTrue(experimental.getInvestments().get(0).getNumShares() == 1);

        assertTrue(experimental.getInvestments().get(1).getTicker().equals("TSLA"));
        assertTrue(experimental.getInvestments().get(1).getNumShares() == 100);

        assertTrue(experimental.getInvestments().get(2).getTicker().equals("GOOGL"));
        assertTrue(experimental.getInvestments().get(2).getNumShares() == 3);

        assertTrue(experimental.getInvestments().get(3).getTicker().equals("F"));
        assertTrue(experimental.getInvestments().get(3).getNumShares() == 7);

    }

    @Test
    public void testAddInvestment(){
        //New investments in stocks that already exist in the list should result in an update in the number of shares
        //NO new investments appended to the list! except in the case of a brand new ticker being added to the investment list
        //all of the checking for this is done in the add method. no need to do it here. Or more importantly, later in the AccounController :)
        control.addInvestment(new Investment(5, "AMZN", SQL.GetTimeStamp()));
        assertEquals(control.getInvestments().get(0).getNumShares(),6);

        control.addInvestment(new Investment(501, "TSLA", SQL.GetTimeStamp()));
        assertEquals(control.getInvestments().get(1).getNumShares(),601);

        control.addInvestment(new Investment(20, "GOOGL", SQL.GetTimeStamp()));
        assertEquals(control.getInvestments().get(2).getNumShares(),23);
        assertFalse(control.isInvested("BDX"));

        assertEquals(4, control.getInvestments().size());

        //this investment is new and therefore should be appended to the list!~
        control.addInvestment(new Investment(10, "BDX", SQL.GetTimeStamp()));
        assertEquals(5,control.getInvestments().size());
        assertTrue(control.isInvested("BDX"));

    }

    @Test
    public void testIsInvested(){
        assertFalse(control.isInvested("Brett"));
        assertFalse(control.isInvested("AAPL"));
        assertFalse(control.isInvested("FORD"));

        assertTrue(control.isInvested("AMZN"));
        assertTrue(control.isInvested("TSLA"));
        assertTrue(control.isInvested("GOOGL"));
        assertTrue(control.isInvested("F"));
    }

    @Test
    public void testGetInvestment(){
        assertNull(control.getInvestment("Brett"));
        assertNull(control.getInvestment("AAPL"));
        assertNull(control.getInvestment("FORD"));

        assertTrue(control.getInvestment("TSLA").getNumShares()==100);
        assertTrue(control.getInvestment("F").getNumShares()==7);
        assertTrue(control.getInvestment("GOOGL").getNumShares()==3);

    }

    @Test
    public void testRemoveInvestment(){
        assertEquals(4, control.getInvestments().size());
        assertTrue(control.isInvested("GOOGL"));
        control.removeInvestment("GOOGL");
        assertFalse(control.isInvested("GOOGL"));
        assertEquals(3, control.getInvestments().size());


        assertEquals(3, control.getInvestments().size());
        assertTrue(control.isInvested("AMZN"));
        control.removeInvestment("AMZN");
        assertFalse(control.isInvested("AMZN"));
        assertEquals(2, control.getInvestments().size());

        assertEquals(2, control.getInvestments().size());
        assertTrue(control.isInvested("F"));
        control.removeInvestment("F");
        assertFalse(control.isInvested("F"));
        assertEquals(1, control.getInvestments().size());
    }



}