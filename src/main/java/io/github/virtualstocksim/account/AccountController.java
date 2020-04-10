package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;


public class AccountController {
    // account instance
    private Account acc;
    private static final Logger logger = LoggerFactory.getLogger(Account.class);



    public void setModel (Account acc){
        this.acc=acc;
    }


    public static Optional<Account> login(String username, String password)
    {
        logger.info("Logging user " + username + " in...");

        try(Connection conn = AccountDatabase.getConnection();
            CachedRowSet passwordCheckRS = SQL.executeQuery(conn, String.format("SELECT password_hash, password_salt " +
                    "from accounts " +
                    " where username = ? "), username);)
        {
            if(!passwordCheckRS.next()){
                return Optional.empty();
            }
            boolean isValid = Encryption.validateInput(password.toCharArray(), passwordCheckRS.getBytes("password_salt"), passwordCheckRS.getBytes("password_hash"));

            // check if credentials are valid
            if (isValid) {
                return Account.Find(username);
            }else{
                return Optional.empty();
            }

        } catch (SQLException e){
            logger.error("Error while parsing result from accounts database\n", e);

        }
        return Optional.empty();
    }



    /**
     *
     * @param accountID Account id to update
     * @param newPicturePath Updated picture name
     */
    public void updateProfilePicture(int accountID, String newPicturePath, byte[] picture) {


        // allow user to provide a new picture and update it
        // convert picture name to UUID
        // how to upload picture to server (could be bytes?)

    }


    public void updateUserBio(int accountID, String newBio){
       Account acc = Account.Find(accountID).get();
       acc.setBio(newBio);
       try{
           acc.update();
           logger.info("Bio updated successfully!");
       } catch(SQLException e){
           logger.error("Error: " + e.toString());
       }

    }


}
