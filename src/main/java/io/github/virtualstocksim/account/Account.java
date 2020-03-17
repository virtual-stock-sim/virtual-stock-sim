package io.github.virtualstocksim.account;

import io.github.virtualstocksim.stock.Stock;

import java.util.ArrayList;

public class Account {
    private String uname;
    private String pword;
    private String email;
    private String bio;
    private int id;
    private int leaderboardRank;
    private ArrayList<Stock> stocksFollowed;
    private AccountType type;


    public Account() {
        stocksFollowed = new ArrayList<Stock>();
    }

    public String getUname(){
        return this.uname;
    }

    public String getPword(){
        return this.pword;
    }

    public String getEmail(){
        return this.email;
    }

    public void setUname(String uname){
        this.uname = uname;
    }

    public void setPword(String pword){
        this.pword = pword;
    }

    public void setEmail(String email){
        this.email = email;
    }

    // this method should return a list of transactions (I think?)
    public void getTransactionHistory () {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
