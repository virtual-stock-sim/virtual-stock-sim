package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionHistory;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.Assert.*;

public class AccountControllerTest
{
    private AccountController conn;
    StocksFollowed stocksFollowed;
    Stock Amazon;
    List<Transaction> transactions;
    TransactionHistory transactionHistory;
    List<Stock> stocks;
    String uuid;

    private static final String TEST_PASSWORD = "virtualstocksim";

    @Before
    public void setup() {
        conn = new AccountController();

        uuid = UUID.randomUUID().toString();
        stocks = new LinkedList<Stock>();

       // List <Follow>followList = new LinkedList<Follow>();
       // followList.add(new Follow(new BigDecimal(100), DummyStocks.GetDummyStock(DummyStocks.StockSymbol.TESLA)));
       // stocksFollowed = new StocksFollowed(followList);

        //transactions = new LinkedList<Transaction>();
        //transactionHistory = new TransactionHistory(transactions);

        //transactions.add(new Transaction(TransactionType.BUY,"3/18/2020",new BigDecimal("1800.00"),5, Amazon));

        try(Connection conn = AccountDatabase.getConnection())
        {
            SQL.executeUpdate(conn, "DELETE FROM accounts WHERE username = ? ", "TestAdmin");
        }
        catch (SQLException e)
        {
            fail();
        }

        conn.setModel(Account.Create("TestAdmin", "test@vss.com", "supersecret", AccountType.ADMIN).get());
    }

    @Test
    public void testGetModel() {
        Optional<Account> find_acc = Account.Find("TestAdmin");
        if(!find_acc.isPresent()){ fail(); }
        assertEquals(find_acc.get().getUUID(),conn.getModel().getUUID());
        assertEquals(find_acc.get().getType().getText(),conn.getModel().getType().getText());
        assertEquals(find_acc.get().getEmail(),conn.getModel().getEmail());
        assertArrayEquals(find_acc.get().getPasswordHash(), conn.getModel().getPasswordHash());
        assertArrayEquals(find_acc.get().getPasswordSalt(), conn.getModel().getPasswordSalt());
        assertEquals(find_acc.get().getCreationDate(),conn.getModel().getCreationDate());
        assertEquals(find_acc.get().getLeaderboardRank(),conn.getModel().getLeaderboardRank());
        assertEquals(find_acc.get().getBio(),conn.getModel().getBio());
        assertEquals(find_acc.get().getProfilePicture(),conn.getModel().getProfilePicture());

    }

    @Test
    public void testLogin() {
        assertTrue((AccountController.login(conn.getModel().getUsername(), TEST_PASSWORD)));

    }

    @Test
    public void testUpdateBio() {
        conn.updateUserBio("Software Engineer");
        assertEquals(conn.getModel().getBio(), "Software Engineer");
    }

    @Test
    public void testUpdateUsername() {
        conn.updateUsername("DanPalm5");
        assertEquals(conn.getModel().getUsername(), "DanPalm5");
    }

    @Test
    public void testUpdatePassword(){
        conn.updatePassword("ultrasecret");
        Optional<Account> find_acc = Account.Find("TestAdmin");
        if(!find_acc.isPresent()){ fail(); }
        byte[] salt = find_acc.get().getPasswordSalt();
        byte[] hash = find_acc.get().getPasswordHash();
        byte[] pwordHash = Encryption.hash("ultrasecret".toCharArray(), salt);
        assertArrayEquals(hash, pwordHash);
    }

}
