package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//if any tests break dealing with account, it could be because of the data I hand populated into the account test DB, my apologies
public class ResetTokenTest {
    private List<ResetToken> resetTokenList = new LinkedList<ResetToken>();
    private List<Account> accountList = new LinkedList<Account>();
    private List<String> tokens = new LinkedList<String>();
    Calendar calendar = Calendar.getInstance();



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

        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 1);

        for(int i=0; i<7;i++) {
            Account.Create("TestUser"+i,"TestUser"+i+"@vss.com,", String.valueOf(i*2),AccountType.USER);
        }

        this.accountList=Account.FindAll();

        for(int i=0;i<7;i++){
            ResetToken.Create(this.accountList.get(i).getId(), Encryption.getNextSalt(), new Timestamp(calendar.getTimeInMillis()));
            //ResetToken.Create(this.accountList.get(i))
        }
        this.resetTokenList=ResetToken.FindAll();
        for(ResetToken resetToken : this.resetTokenList){
            tokens.add(resetToken.getToken());
        }
    }


    @Test
    public void testGetAccountID(){
        for(int i=0;i<this.resetTokenList.size();i++){
            assertEquals(this.resetTokenList.get(i).getAccountId() , accountList.get(i).getId());
            assertEquals(this.resetTokenList.size(),this.accountList.size());
        }
    }

    @Test
    public void testGetToken(){
        for(int i=0;i<this.resetTokenList.size();i++){
            assertEquals(resetTokenList.get(i).getToken(),resetTokenList.get(i).getToken());
        }
    }

    @Test
    public void testSetToken() throws SQLException {
        //in real life, these strings are securely generated randoms
        resetTokenList.get(0).setToken("Open the pod bay doors HAL");
        resetTokenList.get(1).setToken("Im sorry dave Im afraid i cant let you do that");
        resetTokenList.get(2).setToken("I know you and Frank were planning to disconnect me");

        //these should remain the same
        for(int i=3;i<resetTokenList.size();i++){
            assertEquals(resetTokenList.get(i).getToken(),tokens.get(i));
        }
        assertEquals("Open the pod bay doors HAL", resetTokenList.get(0).getToken());
        assertEquals("Im sorry dave Im afraid i cant let you do that", resetTokenList.get(1).getToken());
        assertEquals("I know you and Frank were planning to disconnect me", resetTokenList.get(2).getToken());
    }

    //need to figure out a better way to do this test
    @Test
    public void testGetExpiration(){
        //since the expiration time is 1h
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.setTime(new Date());
        calendarMax.add(Calendar.MINUTE, 61);

        Calendar calendarMin = Calendar.getInstance();
        calendarMin.setTime(new Date());
        calendarMin.add(Calendar.MINUTE, 58);

        for(int i=0;i<resetTokenList.size();i++){
            assertTrue( resetTokenList.get(i).getExpiration().getTime()<new Timestamp(calendarMax.getTimeInMillis()).getTime());

            assertTrue( resetTokenList.get(i).getExpiration().getTime()>new Timestamp(calendarMin.getTimeInMillis()).getTime());
        }
    }

    @Test
    public void testSetExpiration(){
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.setTime(new Date());
        calendarMin.add(Calendar.MILLISECOND, 1);

        for(ResetToken resetToken: this.resetTokenList){
            resetToken.setExpiration(new Timestamp(calendarMin.getTimeInMillis()));
        }

        Calendar newCal = Calendar.getInstance();
        newCal.setTime(new Date());

        for(ResetToken resetToken : this.resetTokenList){
            assertTrue(resetToken.getExpiration().getTime() > new Timestamp(newCal.getTimeInMillis()).getTime() );
        }
    }





    //tests for delete, update, ect.
    //try to get to 100% coverage


}
