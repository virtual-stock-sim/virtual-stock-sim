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

    public  List<Map.Entry<String, BigDecimal>> getUsernameValuePair(){
        return this.usernameValuePair;
    }

    //look into negating * in the future after boilerplate is written...
    public void pullAccountsFromDB(){
       this.accounts= Account.FindCustom("SELECT * FROM account WHERE leaderboard_rank > -1");
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
            Account tempAccount = Account.Find(usernameValuePair.get(i).getKey()).orElse(null);
            System.out.println("Found account "+ tempAccount.getEmail());
/*            if(tempAccount==null){
                //this would have to be a really weird case to trigger this
                throw new TradeException("Account not found in database", TradeExceptionType.USER_NOT_FOUND);
            }*/
            tempAccount.setLeaderboardRank(i+1);
            tempAccount.update();
        }
    }

}
