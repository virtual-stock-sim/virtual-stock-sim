package io.github.virtualstocksim.leaderboard;

import io.github.virtualstocksim.account.Account;

import java.util.LinkedList;
import java.util.List;

public class LeaderBoard {

    private List <Account> accounts = new LinkedList<>();

    public void addAccount(Account a ){
        accounts.add(a);
    }

    public void setAccountsFromDB(){
        this.accounts= Account.FindAll();
    }

    //remove account by username
    public void removeAccount(String username){
        for(int i=0;i<this.accounts.size();i++){
            if(accounts.get(i).getUsername().equals(username)){
                this.accounts.remove(i);
            }
        }
    }

    //remove account from leader board by reference to the account
    public void removeAccount(Account a ){
        for(int i=0;i<this.accounts.size();i++){
            if(accounts.get(i).getUUID().equals(a.getUUID())){
                this.accounts.remove(i);
            }
        }
    }

    //should be called by scheduler
    //probably only want to run once a day, just because
    //this is probably going to be expensive
    public void updateRanks(){
        accounts = Account.FindAll();
        
    }

}
