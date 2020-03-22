package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.SqlCmd;
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
            CachedRowSet passwordCheckRS = SqlCmd.executeQuery(conn, String.format("SELECT password_hash, password_salt " +
                    "from accounts " +
                    " where username = ? "), username);
        )
        {

            boolean isValid = Encryption.validateInput(password.toCharArray(), passwordCheckRS.getBytes("password_salt"), passwordCheckRS.getBytes("password_hash"));

            // check if credentials are valid
            if (isValid) {
                return Account.find(username);
            }

        } catch (SQLException e){
            logger.error("Error while parsing result from accounts database\n", e);

        }
        return Optional.empty();
    }



}
