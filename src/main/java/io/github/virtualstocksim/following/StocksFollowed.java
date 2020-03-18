package io.github.virtualstocksim.following;

import io.github.virtualstocksim.stock.Stock;

import java.util.LinkedList;
import java.util.List;

public class StocksFollowed {

    private List<Stock> stocksFollowed;

    public StocksFollowed(List<Stock> stocksFollowed)
    {
        this.stocksFollowed = stocksFollowed;
    }

    public StocksFollowed(String stocksFollowed)
    {
        this(parseStocksFollowed(stocksFollowed));
    }

    public List<Stock> getStocksFollowed()
    {
        return this.stocksFollowed;
    }

    public void setStocksFollowed(List<Stock> stocksFollowed)
    {
        this.stocksFollowed = stocksFollowed;
    }

    public void setStocksFollowed(String stocksFollowed)
    {
        setStocksFollowed(parseStocksFollowed(stocksFollowed));
    }

    //TODO: Not sure if this should take an id/symbol instead of stock instance
    // for same reason that removeStock takes an id/symbol instead of stock instance
    public void addStock(Stock stock)
    {
        this.stocksFollowed.add(stock);
    }

    public void removeStock(int id)
    {
        this.stocksFollowed.removeIf(s -> s.getId() == id);
    }

    public void removeStock(String symbol)
    {
        this.stocksFollowed.removeIf(s -> s.getSymbol().equals(symbol));
    }

    ///TODO: Implement
    /**
     * Parses a string of comma seperated stock ids and returns stock instances
     * @param stocksFollowed String containing comma seperated stock ids
     * @return List of stock instances
     */
    private static List<Stock> parseStocksFollowed(String stocksFollowed)
    {

        return new LinkedList<>();
    }

}
