package io.github.virtualstocksim.following;

import io.github.virtualstocksim.stock.Stock;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class StocksFollowed {

    private List<Follow> stocksFollowed;

    public StocksFollowed(List<Follow> stocksFollowed)
    {
        this.stocksFollowed = stocksFollowed;
    }


    public List<Follow> getStocksFollowed()
    {
        return this.stocksFollowed;
    }

    public void setStocksFollowed(List<Follow> stocksFollowed)
    {
        this.stocksFollowed = stocksFollowed;
    }


    public void setFollow(Follow newFollow)
    {
        this.stocksFollowed.add(newFollow);
    }

    public void removeFollow(Follow toRemove)
    {
        this.stocksFollowed.remove(toRemove);
    }



}
