package io.github.virtualstocksim.leaderboard;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class TopStocksTest {
    TopStocks topStocks;
    @Before
    public void setup(){
        topStocks = new TopStocks();
    }

    //this test provides coverage to the sorting function as well
    @Test
    public void testSortStocks(){
        List<Map.Entry<String, Double>> topFiveStocks = topStocks.getTopFiveStocks();
        assertTrue(topFiveStocks.get(0).getKey().equals("AMZN"));
        assertTrue(topFiveStocks.get(1).getKey().equals("GOOGL"));
        assertTrue(topFiveStocks.get(2).getKey().equals("TSLA"));
        assertTrue(topFiveStocks.get(3).getKey().equals("BDX"));
        assertTrue(topFiveStocks.get(4).getKey().equals("F"));

    }

    //Changed my PrevClose data to
    //0,197,250,600,498


}
