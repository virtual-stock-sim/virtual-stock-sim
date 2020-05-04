package io.github.virtualstocksim.leaderboard;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.TradeException;
import io.github.virtualstocksim.account.TradeExceptionType;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LeaderBoard {

    private List <Account> accounts = new LinkedList<>();

    List<Map.Entry<String, BigDecimal>> usernameValuePair = new LinkedList<>();

    public List <Account> getAccounts(){
         return this.accounts;
    }

    public void addAccount(Account a ){
        Account local = Account.Find(a.getId()).orElseGet(null);
        if(local==null){
            throw new TradeException("That user is not in the database", TradeExceptionType.USER_NOT_FOUND);
        }
        local.setLeaderboardRank(1000);
        try {
            local.update();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addAccount(String input) {
        Account local = Account.Find(input).orElseGet(null);
        if(local==null){
            throw new TradeException("That user is not in the database", TradeExceptionType.USER_NOT_FOUND);
        }
        //it makes sense to set the leaderboard rank to something random & low
        //so all the calls to update the board can be in sync .... the user will just have to wait until the next scheduled call
        //as long as the rank is no longer -1 (opt-out)
        local.setLeaderboardRank(1000);
        try {
            local.update();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }



    //look into negating * in the future after boilerplate is written...
    public void pullAccountsFromDB(){
       this.accounts= Account.FindCustom("SELECT * FROM account WHERE leaderboard_rank > -1");
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

    //pulls accts from database and calculates their new ranks
    public void calculateRanks(){
        //first pull all of the accounts
        this.pullAccountsFromDB();
       // System.out.println("Account values: ");
          for(Account acct : this.accounts) {
              usernameValuePair.add(acct.getNameAndValue());
          }
          Collections.sort(usernameValuePair, (a,b) -> b.getValue().compareTo(a.getValue()));
    }

    //this method should be called in the view
    //or any time the board needs to be displayed
    //without other query or calculation
    public List <String> getCurrentRanks(){
        List<String> toReturn = new LinkedList<>();
        List<Account> tempAccounts;
        tempAccounts=Account.FindCustom("SELECT id, username FROM account WHERE leaderboard_rank > -1 ORDER BY leaderboard_rank asc FETCH FIRST 5 ROWS ONLY ");
        for(Account a: tempAccounts ){
            toReturn.add(a.getUsername());
        }
        return toReturn;
    }


    //should be called by a task scheduler
    //probably only want to run once a day, just because
    //this is probably going to be expensive, and ranks won't change much day-to-day

    //complimentary to the calculateRanks method, just pushes result of calculation to DB
    public void updateRanks() throws SQLException {
        this.calculateRanks();
        //this loop might be dangerous ... ask team lead

        for(int i=0; i<usernameValuePair.size();i++){

            Account temp =Account.Find(usernameValuePair.get(i).getKey()).orElseGet(null);
            if(temp==null){
                //this would have to be a really weird case to trigger this
                throw new TradeException("Account not found in database", TradeExceptionType.USER_NOT_FOUND);
            }
            temp.setLeaderboardRank(i+1);
            temp.update();
        }
    }

}
