package io.github.virtualstocksim.account;
import java.io.File;


import io.github.virtualstocksim.config.Config;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.encryption.Encryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class PasswordResetManager {
    private String username;
    private Account account;
    private String email;
    private String UUID;
    private String resetLink;
    private String resetSalt;

    //this class gets a random salt, sends the user a link to virtualstocksim.com/reset?token=<Salt_Generated>
    //virtualstocksim.com/reset/returned_from_this_message
    public String getResetSalt(){
        return this.resetSalt;
    }
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetManager.class);
    public String getResetLink(){
        return this.resetLink;
    }
    public String getUsername(){
        return this.username;
    }
    public String getEmail(){return this.email;}

    

    //uses two other methods to either set the email an email to the user's input in the case they put in an email address
    //or look up the email address from an account name
    public void setDestEmail(String input){

       if(this.isEmail(input)) {
           this.email = input;
       }else if(this.isUsername(input)){
            //shouldn't need the null check here since it is
            //already handled in the checkifusername function
            this.email=Account.Find(input).orElse(null).getEmail();
        }else{
            //for security reasons, we should not let the user know that
            //an email was sent or not (Confirming that we do or do not have an email in our DB)
            //so, we should just let the mail sender throw an error as long as the view has no notification
           logger.info("No account exists under that username or email!");
            this.email=null;
       }

    }

    public boolean isEmail(String s){

       List <Account> tempAccountList  = Account.FindCustom("SELECT id, username, email FROM account WHERE email = ?",s.trim());
        if(!tempAccountList.isEmpty() && !s.trim().isEmpty() && s.contains("@") ) {
            if(tempAccountList.get(0).getEmail().equals(s.trim())) {
                this.account=tempAccountList.get(0);
                this.username=tempAccountList.get(0).getUsername();
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public boolean isUsername(String s){
        Account tempAccount = Account.Find(s.trim()).orElse(null);
        if( !s.trim().isEmpty() && tempAccount!=null && tempAccount.getUsername().equals(s.trim())){
            //what the username entered is username
            this.username=tempAccount.getUsername();
            this.account=tempAccount;
            return true;
        }else {
            //what the user entered is not a username
            return false;
        }
    }

    public static void deleteTokenFromDB(String token){
        try{
            Connection conn = AccountDatabase.getConnection();
            SQL.executeUpdate(conn, "DELETE FROM reset_token where token = ?",token);
            logger.info("token deleted. User has to generate a new one");
        } catch (SQLException e) {
           logger.error(e.getMessage());
        }
    }

    public boolean isExpired(String s){
        ResetToken token = ResetToken.Find(s).orElse(null);
        if(token!=null) {
            int x = SQL.GetTimeStamp().compareTo(token.getExpiration());
            if (x > 0) {
                //delete the timestamp, it expired
                logger.info("That timestamp has expired!");
                    deleteTokenFromDB(token.getToken());
                    return true;
            }
        }
        return false;
    }

    public String generateResetLink() throws UnsupportedEncodingException {
        //delete all the existing tokens for that acct
        try {
            Connection conn = AccountDatabase.getConnection();
            SQL.executeUpdate(conn, "DELETE FROM reset_token WHERE account_id =?", this.account.getId());
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        //use the built in calendar class to set the expiration
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 1);
        //the two lines below may need some clean-up
        ResetToken rt = ResetToken.Create(this.account.getId(), Encryption.getNextSalt(), new Timestamp(calendar.getTimeInMillis())).orElse(null);

        if (rt != null) {
            logger.info("generated by rt: " + rt.getToken());
            //will need to change this in the future to virtualstocksim.com
            this.resetLink = "http://localhost:8081/reset?token=" + rt.getToken();
            return resetLink;
        } else {
            logger.error("There was an exception created by trying to create a new reset Token");
            return null;
        }
    }

    //get email address and password from command line arguments
    private final String from = Config.getConfig("email.addr");
    private final String password = Config.getConfig("email.pass");


    public void sendMailWithLink(){
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });
    if(this.email!=null) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("virtualstocksim@gmail.com"));
            //send to the email associated with this PasswordResetManager object
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(this.email));
            message.setSubject("VSS - requested password reset");
            //here is where the local variables will be set and a new salt generated
            final String EMAIL_TEXT = "Hello, a password  was recently requested for the account associated with this email on virtualstocksim.com Please click on the following link to reset your password. This link will expire in one hour! \n" + this.generateResetLink();
            message.setText(EMAIL_TEXT);
            Transport.send(message);
            logger.info("done sending message! destination was: " + this.email);
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }else{
       logger.warn("User does not exist in system (do not notify frontend)");
        //again, stay quiet on front end
    }
    }




}
