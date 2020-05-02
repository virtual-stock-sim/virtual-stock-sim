package io.github.virtualstocksim.account;
import java.io.File;


import io.github.virtualstocksim.encryption.Encryption;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class PasswordResetManager {
    private String username;
    private Account account;
    private String email;
    private String UUID;
    private String resetLink;
    private String resetSalt;


    private final String SUBJECT_LINE = "Password reset - VSS";


/*    public PasswordResetManager(String username){
        this.account=Account.Find(username).orElseGet(null);
        if(account!=null){
            this.email=account.getEmail();
            this.username=username;
            this.UUID = account.getUUID();
        }else{
            System.out.println("There is no account with that username in the DB. (However, this should not be made public information via frontend for security reasons)");
        }
    }*/

    //this will be used to generate the actual page. i.e:
    //virtualstocksim.com/reset/returned_from_this_message
    public String getResetSalt(){
        return this.resetSalt;
    }

    public String getResetLink(){
        return this.resetLink;
    }

    public void setEmail(String input){
        System.out.println(input);
       if(this.checkIfEmail(input)) {
           this.email = input;

       }else if(this.checkIfUsername(input)){
            //shouldn't need the null check here since it is
            //already handled in the checkifusername function
            this.email=Account.Find(input).get().getEmail();
        }
        else{
            //for security reasons, we should not let the user know that
            //an email was sent or not (Confirming that we do or do not have an email in our DB)
            //so, we should just let the mail sender throw an error as long as the view has no notification
           System.out.println("No account exists under that username or email!");
            this.email=null;
        }

    }

    public boolean checkIfEmail(String s){
        System.out.println(s);
       List <Account> tempAccountList  = Account.FindCustom("SELECT id, username, email FROM account WHERE email = ?",s.trim());
        if(!tempAccountList.isEmpty() && !s.trim().isEmpty() && s.contains("@") ) {
            System.out.println("from list "+tempAccountList.get(0).getEmail());
            if(tempAccountList.get(0).getEmail().equals(s.trim())) {
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public boolean checkIfUsername(String s){
        Account tempAccount = Account.Find(s.trim()).orElseGet(null);
        if(s!=null && !s.trim().isEmpty() && tempAccount!=null && tempAccount.getUsername().equals(s.trim())){
            //what the username entered is username
            return true;
        }else {
            //what the user entered is not a username
            return false;
        }
    }

    public String generateResetLink(){
        String resetSalt = Base64.getEncoder().encodeToString(Encryption.getNextSalt());
        //change later to "http://virtualstocksim.com/reset/reset_salt"
        //keep it it this way until push
        this.resetSalt=resetSalt;
        resetLink= "http://localhost:8081/reset/"+resetSalt;
        return "http://localhost:8081/reset/"+resetSalt;
    }

    //**********************************************************************************************
    //CHANGE THIS ON COMMIT UNTIL COMMAND LINE ARGS CHANGE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //DO NOT FORGET!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private final String from = "virtualstocksim@gmail.com";
    private final String password = "9~)ZJ)9.wu3nW!D";
    //DO NOT FORGET!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //CHANGE THIS ON COMMIT UNTIL COMMAND LINE ARGS CHANGE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //**********************************************************************************************


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
            final String EMAIL_TEXT = "Hello, a password  was recently requested for the account associated with this email on virtualstocksim.com Please click on the following link to reset your password! \n" + this.generateResetLink();
            message.setText(EMAIL_TEXT);
            Transport.send(message);
            System.out.println("done sending message! destination was: " + this.email);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }else{
        System.out.println("User does not exist in system");
        //again, stay quiet on front end
    }
    }

    //after the mail is sent, we would want to auto generate a page with a time to live
    //The path to this page should be the same as this.resetLink

    public static void main(String[] args)  {
        PasswordResetManager prm = new PasswordResetManager();
        System.out.println(prm.resetLink);
        prm.sendMailWithLink();

    }


}
