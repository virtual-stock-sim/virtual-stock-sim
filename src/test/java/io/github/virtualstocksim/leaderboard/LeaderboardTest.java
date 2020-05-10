package io.github.virtualstocksim.leaderboard;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.account.AccountDatabase;
import io.github.virtualstocksim.account.AccountType;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.stock.DummyStocks;
import io.github.virtualstocksim.stock.DummyStocks.StockSymbol;
import io.github.virtualstocksim.transaction.TransactionType;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LeaderboardTest {
    private List<Account> accountList;
    AccountController accountController = new AccountController();
    LeaderBoard leaderboard= new LeaderBoard();
    private List<Map.Entry<String, BigDecimal>> usernameValuePair;
    @Before
    public void setup() throws SQLException {

        try {
            Connection conn = AccountDatabase.getConnection();
            SQL.executeUpdate(conn, "DELETE FROM RESET_TOKEN");
            SQL.executeUpdate(conn, "DELETE FROM ACCOUNT");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(int i=0; i<7;i++) {
            Account.Create("TestUser"+i,"TestUser"+i+"@vss.com,", String.valueOf(i*2), AccountType.USER);
        }

        this.accountList=Account.FindAll();
        for(Account account : this.accountList){
            //opt in every account
            account.setLeaderboardRank(1000);
            accountController.setModel(account);
            accountController.followStock(DummyStocks.GetDummyStock(StockSymbol.AMAZON));
            accountController.followStock(DummyStocks.GetDummyStock(StockSymbol.TESLA));
            accountController.followStock(DummyStocks.GetDummyStock(StockSymbol.GOOGLE));
            accountController.followStock(DummyStocks.GetDummyStock(StockSymbol.FORD));
            accountController.followStock(DummyStocks.GetDummyStock(StockSymbol.BDX));
        }

        //most in cash (second)
        accountController.setModel(accountList.get(2));
        accountController.getModel().setWalletBalance(new BigDecimal(14000));
        accountList.get(2).update();

        //most in assets and cash (first)
        accountController.setModel(accountList.get(4));
        accountController.trade(TransactionType.BUY,"AMZN",3);
        accountController.getModel().setWalletBalance(new BigDecimal(14000));
        accountList.get(4).update();

        //just holing stocks (third)
        accountController.setModel(accountList.get(6));
        accountController.getModel().setWalletBalance(new BigDecimal(100000));
        accountController.trade(TransactionType.BUY,"TSLA",50);
        accountController.trade(TransactionType.BUY,"AMZN",2);
        accountController.getModel().setWalletBalance(new BigDecimal(0));
        accountList.get(6).update();
        //rest of the top five accounts will have 10k



    }

    @Test
    public void testPullAccountsFromDB(){
        leaderboard.pullAccountsFromDB();
        assertTrue( accountList.size()!=0);
        assertEquals(accountList.size(),leaderboard.getAccounts().size());
    }

    @Test
    public void testCalculateRanks(){
        leaderboard.calculateRanks();
        usernameValuePair=leaderboard.getUsernameValuePair();
        for(int i=0; i<usernameValuePair.size();i++){
           // System.out.println("Look "+usernameValuePair.get(i));
        }
        assertTrue(usernameValuePair.get(0).getKey().equals(accountList.get(4).getUsername()));
        assertTrue(usernameValuePair.get(1).getKey().equals(accountList.get(2).getUsername()));
        assertTrue(usernameValuePair.get(2).getKey().equals(accountList.get(6).getUsername()));

    }

    @Test
    public void testUpdateRanks() throws SQLException {
        //call to calc ranks is in the update function
        leaderboard.updateRanks();

        assertEquals(Account.Find("TestUser4").get().getLeaderboardRank(),1);
        assertEquals(Account.Find("TestUser2").get().getLeaderboardRank(),2);
        assertEquals(Account.Find("TestUser6").get().getLeaderboardRank(),3);

    }
}
