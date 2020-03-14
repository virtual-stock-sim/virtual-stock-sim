package io.github.virtualstocksim.transaction;

public class Transaction {
    private String date;
    private double pricePerShare;
    private int numShares;
    public Transaction (String date, double pricePerShare,int numShares){
        this.date=date;
        this.pricePerShare=pricePerShare;
        this.numShares=numShares;
    }
}
