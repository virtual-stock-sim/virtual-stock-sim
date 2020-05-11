package io.github.virtualstocksim.leaderboard;

import io.github.virtualstocksim.stock.ResetStockDB;
import io.github.virtualstocksim.stock.Stock;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class TopStocksTest {
    TopStocks topStocks;
    private List<Stock> stocks;
    @Before
    public void setup() throws SQLException {
        ResetStockDB.reset();

        //to cover more cases, (ex: close higher than currprice) I am adding data manually
       this.stocks=Stock.FindAll();
        stocks.get(0).setPrevClose(new BigDecimal(0));
        stocks.get(1).setPrevClose(new BigDecimal(197));
        stocks.get(2).setPrevClose(new BigDecimal(250));
        stocks.get(3).setPrevClose(new BigDecimal(600));
        stocks.get(4).setPrevClose(new BigDecimal(498));

        for(Stock stock : this.stocks){
            stock.update();
        }


        topStocks = new TopStocks();
    }

    //this test provides coverage to the sorting function as well
    @Test
    public void testSortStocks(){
        List<Map.Entry<String, Double>> topFiveStocks = topStocks.getTopFiveStocks();

        for(int i=0; i<topFiveStocks.size();i++){
            System.out.println(topFiveStocks.get(i));
        }


        assertTrue(topFiveStocks.get(0).getKey().equals("AMZN"));
        assertTrue(topFiveStocks.get(1).getKey().equals("GOOGL"));
        assertTrue(topFiveStocks.get(2).getKey().equals("TSLA"));
        assertTrue(topFiveStocks.get(3).getKey().equals("BDX"));
        assertTrue(topFiveStocks.get(4).getKey().equals("F"));

    }

    //Changed my PrevClose data to
    //0,197,250,600,498


}
