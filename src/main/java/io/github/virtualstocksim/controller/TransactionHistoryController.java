package io.github.virtualstocksim.controller;
import io.github.virtualstocksim.model.TransactionHistory;
public class TransactionHistoryController {
    private TransactionHistory model;
    //this class is in a really weird state right now
    //because there are no stock objects
    //but it DOES exist, so


    public void setModel (TransactionHistory model){
        this.model=model;
    }


}
