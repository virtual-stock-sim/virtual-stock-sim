package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import org.junit.Before;
import org.junit.Test;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class PasswordResetManagerTest {
    private PasswordResetManager prm = new PasswordResetManager();

    private List<Account> accountList = new LinkedList<>();

    private ResetToken expectedExpired,expectedValid;
    @Before
    public void setup(){

        try {
            Connection conn = AccountDatabase.getConnection();
            SQL.executeUpdate(conn, "DELETE FROM RESET_TOKEN");
            SQL.executeUpdate(conn, "DELETE FROM ACCOUNT");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(int i=0; i<7;i++) {
            Account.Create("TestUser"+i,"TestUser"+i+"@vss.com", String.valueOf(i*2),AccountType.USER);
        }

        this.accountList=Account.FindAll();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        //the two lines below may need some clean-up
        expectedExpired = ResetToken.Create(accountList.get(0).getId(), Encryption.getNextSalt(), new Timestamp(calendar.getTimeInMillis())).orElse(null);

        calendar.add(Calendar.HOUR, 1);
         expectedValid = ResetToken.Create(accountList.get(1).getId(), Encryption.getNextSalt(), new Timestamp(calendar.getTimeInMillis())).orElse(null);
    }

    @Test
    public void testCheckIfEmail(){
        PasswordResetManager prm = new PasswordResetManager();
        for(int i=0;i<7;i++) {
            assertTrue(prm.isEmail("TestUser"+i+"@vss.com"));
            assertFalse(prm.isEmail("TestUser"+i));
        }
        assertFalse(prm.isEmail("bkearney1@ycp.edu"));
        assertFalse(prm.isEmail("testUser100@ycp.edu"));
        assertFalse(prm.isEmail("TestUser9@vss.com"));
    }

    @Test
    public void testCheckIfUsername(){
        for(int i=0;i<7;i++) {
            assertTrue(prm.isUsername("TestUser"+i));
            assertFalse(prm.isUsername("TestUser"+i+"@vss.com"));
        }
        assertFalse(prm.isUsername("bkearney1@ycp.edu"));
        assertFalse(prm.isUsername("bkearney1"));
        assertFalse(prm.isUsername("HAL9000"));

    }


    @Test
    public void testSetEmail(){
        //case that user input is an email
        for(int i=0;i<7;i++) {
            prm.setDestEmail("TestUser"+i+"@vss.com");
            assertEquals("TestUser"+i+"@vss.com", prm.getEmail());
        }

        //case that user input username and we need to find the email
        for(int i=0;i<7;i++) {
            prm.setDestEmail("TestUser"+i);
            assertEquals("TestUser"+i+"@vss.com", prm.getEmail());
        }
    }

    @Test
    public void testIsExpired(){
        assertTrue(prm.isExpired(expectedExpired.getToken()));
        assertFalse(prm.isExpired(expectedValid.getToken()));

    }
    //I can't really think of a good way to test the last two functions because they're kind of
    //devoted to sending emails, but open to suggestions

}
