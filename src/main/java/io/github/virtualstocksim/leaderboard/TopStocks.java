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

    public List<Stock> pullStocksFromDB() {
        this.stocks = Stock.FindAll();
        return stocks;
    }

    public List<Map.Entry<String, Double>> sortStocks() {

        for (Stock stock : this.stocks) {
            symbolChangePair.add(stock.getSymbolAndPercentChange());
        }
        Collections.sort(symbolChangePair, (a, b) -> b.getValue().compareTo(a.getValue()));

        return symbolChangePair;
    }

    public List<Map.Entry<String, Double>> getTopFiveStocks() {
        this.pullStocksFromDB();
        this.sortStocks();
        List<Map.Entry<String, Double>> topFiveStocks = new LinkedList<>();
        int max;
        //this check makes sure that the program does not crash
        //if there are less than 5 stocks in the DB
        if (symbolChangePair.size() > 4) {
            max = 5;
        } else {
            max = symbolChangePair.size();
        }
        for (int i = 0; i < 5; i++) {
            topFiveStocks.add(symbolChangePair.get(i));
        }
        return topFiveStocks;
    }
    //convert the calculated ranks into a formatted string so it is easy to display on the view
}
