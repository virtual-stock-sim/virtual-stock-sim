package io.github.virtualstocksim.model;
import io.github.virtualstocksim.stock.Stock;
public class TransactionHistoryModel {



    public TransactionHistoryModel() {
    }


    //this method might not be used later. I'm using it to populate information for ms1
    //for MS1 in this class, a stock ID will only correlate with a position in an array
    //since there are no stock objects at this time, there will be 3 seperate arrays

    private String tickerList[] = {"TSLA", "F", "DD", "AAPL", "GOOGL"};
    private double boughtPriceList[] = {360, 17, 123, 400, 51.3};
    private double currentPriceList[] = {1, 2, 3, 4, 5};
    private int numSharesList[] = {100, 200, 300, 400, 500};
    private String StockInfoToDisplay="";
    private Stock stockList[] = new Stock[5];

    public double getVolumePrice(int stockID) {
        return boughtPriceList[stockID] * numSharesList[stockID];
    }

    public double getBoughtPrice(int stockID) {
        return boughtPriceList[stockID];
    }

    public String[] getTicker() {
        return tickerList;
    }

    public Stock[] getStockHTML() {
        for(int i=1;i<6;i++) {
            stockList[i-1]=Stock.GetStock(i).get();
        }
        return stockList;
    }

    public void setNumShares(int x,int stockID) {

        numSharesList[stockID]=x;
    }
    public void setBoughtPrice(double x,int stockID){

        boughtPriceList[stockID]=x;
    }

    public void setCurrentPrice(double x,int stockID){

        currentPriceList[stockID]=x;
    }






}
