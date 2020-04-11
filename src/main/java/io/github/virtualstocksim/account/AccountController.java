package io.github.virtualstocksim.account;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.sql.rowset.CachedRowSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;


public class AccountController {
    // account instance
    private Account acc;
    private static final Logger logger = LoggerFactory.getLogger(Account.class);

    public AccountController() {

    }


    public Account getModel(){
        return this.acc;
    }

    public void setModel (Account acc){
        this.acc=acc;
    }

    /**
     *
     * @param username username provided
     * @param password  password provided - will be hashed and checked against that stored in database
     * @return Account found with specified username and password parameters, if any
     */
    public static boolean login(String username, String password)
    {
        logger.info("Logging user " + username + " in...");

        Optional<Account> acc = Account.Find(username);
        if(!acc.isPresent()){
            return false;
        }
        // check hash and salt against login credentials
        boolean isValid = Encryption.validateInput(password.toCharArray(), acc.get().getPasswordSalt(), acc.get().getPasswordHash());

        // check if credentials are valid
        if (isValid) {
            logger.info("Logged user "+username+ " in successfully!");
        }else{
            logger.info("Couldn't find account with username "+username);
        }

        return isValid;
    }



    /**
     *
     * @param inputStream  file contents converted to input stream
     * @param fileName file name user uploaded
     */
    public void updateProfilePicture(InputStream inputStream, String fileName) {
        File uploadDir = new File("./userdata/ProfilePictures"); // directory where images are stored
        if(!uploadDir.exists()){
            uploadDir.mkdirs();
        }
        try{
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            ImageIO.write(bufferedImage, fileName.substring(fileName.lastIndexOf("."), fileName.length()-1), uploadDir);

        }catch (IOException e){
            logger.error("Error reading image: " +e);
        }



        try{
            acc.update();
            logger.info("ProfilePicture updated successfully!");
        } catch(SQLException e){
            logger.error("Error: " + e.toString());
        }

    }

    /**
     *
     * @param newUsername - new username that is being stored in database
     */
    public void updateUsername(String newUsername){
        // change username in model
        acc.setUname(newUsername);

        // update username in db
        try{
            acc.update();
            logger.info("Username updated successfully!");
        } catch(SQLException e){
            logger.error("Error: " + e.toString());
        }
    }

    /**
     *
     * @param password password given by user to be hashed and stored in db
     */
    public void updatePassword(String password){
        // generate new hash and salt
        byte[] newSalt = Encryption.getNextSalt();
        byte[] newHash = Encryption.hash(password.toCharArray(),newSalt);

        // update account with newly created hash and salt
        acc.setPasswordHash(newHash);
        acc.setPasswordSalt(newSalt);

        /* update account in database */
        try{
            acc.update();
            logger.info("Password updated successfully!");
        } catch(SQLException e){
            logger.error("Error: " + e.toString());
        }
    }

    /**
     *
     * @param newBio  updated bio given by user to be stored in DB
     */
    public void updateUserBio(String newBio){
       acc.setBio(newBio);
       try{
           acc.update();
           logger.info("Bio updated successfully!");
       } catch(SQLException e){
           logger.error("Error: " + e.toString());
       }

    }


}
