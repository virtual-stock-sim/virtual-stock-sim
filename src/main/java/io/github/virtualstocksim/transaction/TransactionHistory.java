package io.github.virtualstocksim.transaction;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TransactionHistory
{

    private List<Transaction> transactions;
    public TransactionHistory(List<Transaction> transactions)
    {
        this.transactions = new LinkedList<>(transactions);
    }

    public List<Transaction> getTransactions()
    {
        return this.transactions;
    }

    public void setTransactions(Transaction... transactions)
    {
        this.transactions = new LinkedList<>(Arrays.asList(transactions));
    }

    public void addTransaction(Transaction transaction)
    {
        this.transactions.add(transaction);
    }


    /*private String tickerList[] = {"TSLA", "F", "DD", "AAPL", "GOOGL"};
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

    public Stock[] getStockList() {
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
    }*/






}
