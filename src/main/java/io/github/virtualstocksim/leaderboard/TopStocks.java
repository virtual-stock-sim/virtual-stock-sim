package io.github.virtualstocksim.leaderboard;

import io.github.virtualstocksim.stock.Stock;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//This class will be exactly like the LeaderBoard class, but for the top stocks that will be
//displayed on the front page
public class TopStocks {
    //we should also add a field in the DB for the rank of the stock
    private List<Stock> stocks = new LinkedList<>();
    List<Map.Entry<String, Double>> symbolChangePair = new LinkedList<>();
    //this will be very expensive just like the account leaderboard
    //and should eventually be called by the same task scheduler
    public List<Stock> pullStocksFromDB(){
        this.stocks=Stock.FindAll();
        return stocks;
    }

    public List<Map.Entry<String, Double>> getTopFiveStocks(){
        this.pullStocksFromDB();
        for(Stock stock :this.stocks){
            symbolChangePair.add(stock.getSymbolAndPercentChange());
        }
        Collections.sort(symbolChangePair, (a, b) -> b.getValue().compareTo(a.getValue()));
        return symbolChangePair;
    }




}
