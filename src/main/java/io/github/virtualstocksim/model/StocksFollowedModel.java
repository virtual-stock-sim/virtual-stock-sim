package io.github.virtualstocksim.model;

public class StocksFollowedModel {
    //Using hard-coded data and a janky string for HTML, but the core concept is here
    private String tickerList[] = {"TSLA", "F", "DD", "AAPL", "GOOGL"};
    private double boughtPriceList[] = {360, 17, 123, 400, 51.3};
    private double currentPriceList[] = {1, 2, 3, 4, 5};
    private int numSharesList[] = {100, 200, 300, 400, 500};
    private String StockInfoToDisplay="";

    public double getPercentChange(int stockID) {
        return ((currentPriceList[stockID] - boughtPriceList[stockID]) / this.boughtPriceList[stockID]) * 100;
    }
    public double getCurrentPrice(int stockID){
        return currentPriceList[stockID];
    }

    public String getFollowTest(){
        return "Hey, it worked";
    }

}
