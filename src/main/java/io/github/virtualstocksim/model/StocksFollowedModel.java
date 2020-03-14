package io.github.virtualstocksim.model;

import java.text.DecimalFormat;

public class StocksFollowedModel {
    //Using hard-coded data and a janky string for HTML, but the core concept is here
    private String tickerList[] = {"TSLA", "F", "DD", "AAPL", "GOOGL"};
    private double boughtPriceList[] = {360, 17, 123, 400, 51.3};
    private double currentPriceList[] = {132, 25, 38, 423, 5113};
    private int numSharesList[] = {100, 200, 300, 400, 500};
    private String StockInfoToDisplay="";

    public double getPercentChange(int stockID) {
        return ((currentPriceList[stockID] - boughtPriceList[stockID]) / this.boughtPriceList[stockID]) * 100;
    }
    public double getCurrentPrice(int stockID){
        return currentPriceList[stockID];
    }
    public String getFollowedStockHTML(){
        DecimalFormat df = new DecimalFormat("#.##");
        String s="";
        for(int i=0;i<5;i++){
            String localPercentChange = df.format(this.getPercentChange(i));
            s+=("<b>"+tickerList[i]+": </b>" +"Price/Share: "+this.getCurrentPrice(i)+" Percent Change: "+localPercentChange+ "<br/>");
        }
        return s;
    }



}
