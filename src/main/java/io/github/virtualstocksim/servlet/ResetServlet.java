package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.account.PasswordResetManager;
import io.github.virtualstocksim.account.ResetToken;
import io.github.virtualstocksim.encryption.Encryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;

@WebServlet(urlPatterns = {"/reset"})

public class ResetServlet extends HttpServlet {
    private String errorMessage=null;
    public boolean passwordMatch=true;
    private static final long serialVersionUID = 1L;
    //private List<String> resetSalts = ResetToken.FindByAccountId() ;
    private String resetSalt;
    private static final Logger logger = LoggerFactory.getLogger(ResetServlet.class);
    private PasswordResetManager prm = new PasswordResetManager();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        logger.info("Reset servlet: doGet");
        String salt= req.getParameter("token");
        logger.warn("Current salt: "+salt);

        //simply calling this method (isExpired) will delete an expired token
        //so there is no need to change anything in the JSP
        //changing the view based upon the existence of a token works because
        //any invalid tokens will have already been deleted
        boolean isExpired = prm.isExpired(salt);
        req.setAttribute("errorMessage", errorMessage);
        logger.info("Token expiration: isExpired: " + isExpired);
        if(salt!=null && !salt.trim().isEmpty()) {
                if(ResetToken.Find(salt).orElse(null)!=null) {
                    req.setAttribute("salt", salt);
                }else{
                    logger.error("There was an error finding that token int the RESET_TOKEN table");
                }
        }
        req.getRequestDispatcher("/_view/reset.jsp").forward(req, resp);
        req.setAttribute("model",prm);
    }



    @Override
    protected void  doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        logger.info("Reset servlet: doPost");
        //this string could be either a username or password thus generic variable name
        String userInfo=req.getParameter("userInput");
        if(userInfo!=null && !userInfo.trim().isEmpty()) {
            prm.setDestEmail(req.getParameter("userInput"));
            prm.sendMailWithLink();
        }
        String token = req.getParameter("token");
        String pass1= req.getParameter("newPass1");
        String pass2= req.getParameter("newPass2");

        //case when the user first submits the email field
        if(token.trim().isEmpty()){
            //System.out.println("");
            resp.sendRedirect("/login");
        }


       if(pass1!=null && pass2!=null && !token.trim().isEmpty()){//change password AND delete the token from the database
           //main success scenario
           int ID;
           Account localAccount=null;
           if(pass1.equals(pass2) && pass1.length()>=8) {
               ResetToken localToken = ResetToken.Find(req.getParameter("token")).orElse(null);
               if(localToken!=null){
                   ID = localToken.getAccountId();
                   localAccount = Account.Find(ID).orElse(null);
               }else{
                   logger.error("Token from that param returned null");
               }

               if(localAccount!=null) {
                   AccountController accountController = new AccountController();
                   //updating (pushing new info to DB) handled by account controller
                   //no update statements needed here
                   accountController.setModel(localAccount);
                   accountController.updatePassword(pass1);

                   //we need to delete the token once it is used  -- only inside of this if block!
                   PasswordResetManager.deleteTokenFromDB(localToken.getToken());

               }else{
                   logger.error("No account could be found with that ID. Was the account deleted from the DB before a password reset was complete?");
               }
               resp.sendRedirect("/login");
           }else if(pass1.equals(pass2)&& pass1.length()<8){
                errorMessage="Error: Your password must be at least 8 characters long";
               //send the user back to the same exact page with the link parameters
               //to the view the error message, otherwise they would be taken back to the "provide email/username" page
               logger.error("Please ensure that the passwords match! Try again");
               req.setAttribute("errorMessage", errorMessage);
               resp.sendRedirect("/reset?token="+token);
           }else{
                errorMessage="Error: Passwords do not match";
               //send the user back to the same exact page with the link parameters
               logger.error("Please ensure that the passwords match! Try again");
               req.setAttribute("errorMessage", errorMessage);
                resp.sendRedirect("/reset?token="+token);
           }

       }
    }



}
