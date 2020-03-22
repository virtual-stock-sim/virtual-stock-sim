package io.github.virtualstocksim.account;
import io.github.virtualstocksim.database.DatabaseException;
import io.github.virtualstocksim.encryption.Encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class AccountController {
    // account instance
    private Account acc;

    private static final Logger logger = LoggerFactory.getLogger(Account.class);
    private static AccountDatabase accountDatabase = AccountDatabase.Instance();

    public void setModel (Account acc){
        this.acc=acc;
    }


    public static Optional<Account> login(String username, String password)
    {
        try {
            logger.info("Logging user " + username + " in...");
            ResultSet passwordCheckRS = accountDatabase.executeQuery(("SELECT password_hash, password_salt " +
                    "from accounts " +
                    " where username = ? "), username);
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

        } catch (DatabaseException e) {
            logger.error("Invalid login. Please try again", e);
        } catch (SQLException e){
            logger.error("Error while parsing result from accounts database\n", e);

        }
        return Optional.empty();
    }



}
