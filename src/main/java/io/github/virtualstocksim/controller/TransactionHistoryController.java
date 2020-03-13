package io.github.virtualstocksim.controller;
import io.github.virtualstocksim.model.TransactionHistoryModel;
public class TransactionHistoryController {
    private TransactionHistoryModel model;
    //this class is in a really weird state right now
    //because there are no stock objects
    //but it DOES exist, so


    public void setModel (TransactionHistoryModel model){
        this.model=model;
    }


}
