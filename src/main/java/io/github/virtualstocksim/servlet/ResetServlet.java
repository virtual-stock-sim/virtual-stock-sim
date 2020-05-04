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
    private static final long serialVersionUID = 1L;
    //private List<String> resetSalts = ResetToken.FindByAccountId() ;
    private String resetSalt;
    private static final Logger logger = LoggerFactory.getLogger(ResetServlet.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        logger.info("Reset servlet: doGet");
        String salt= req.getParameter("token");
        PasswordResetManager prm = new PasswordResetManager();
        //*********** remember,simply calling this method (isExpired) will delete an expired token***********
        //so there is no need to do change anything in the JSP
        //changing the view based upon the existence of a token still works because
        //any invalid ones will have already been deleted
        boolean isExpired = prm.isExpired(salt);

        logger.info("Token expiration: isExpired: " + isExpired);
        if(salt!=null && !salt.trim().isEmpty()) {
                if(ResetToken.Find(salt).orElse(null)!=null) {
                    req.setAttribute("salt", salt);
                }
        }
        req.getRequestDispatcher("/_view/reset.jsp").forward(req, resp);
    }

    @Override
    protected void  doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        logger.info("Reset servlet: doPost");
        String errorMessage = "Transaction went smoothly";
        //this string could be either a username or password thus generic variable name
        String userInfo=req.getParameter("userInput");
        if(userInfo!=null && !userInfo.trim().isEmpty()) {
            PasswordResetManager prm = new PasswordResetManager();
            prm.setEmail(req.getParameter("userInput"));
            prm.sendMailWithLink();
        }
        String token = req.getParameter("token");
        String pass1= req.getParameter("newPass1");
        String pass2= req.getParameter("newPass2");

       if(pass1!=null && pass2!=null && token!=null && !token.trim().isEmpty()  ){//change password AND delete the token from the database
           if(pass1.equals(pass2)) {
               ResetToken local = ResetToken.Find(req.getParameter("token")).get();
               int ID = local.getAccountId();
               Account localAccount = Account.Find(ID).get();
               AccountController ac = new AccountController();
               ac.setModel(localAccount);
               ac.updatePassword(pass1);
               //we need to delete the token once it is used
               PasswordResetManager.deleteTokenFromDB(local.getToken());
               //updating (pushing new info to DB) handled by account controller
           }else{
               boolean passmatchError=true;
               //send the user back to the same exact page with the link poarameters
               req.setAttribute("match",passmatchError);


               logger.error("Please ensure that the passwords match! Try again");

                req.setAttribute("salt",token);
               //req.getRequestDispatcher("/_view/reset.jsp").forward(req, resp);
           }
           req.setAttribute("salt",null);
           req.getRequestDispatcher("/_view/reset.jsp").forward(req, resp);

       }
       logger.info(errorMessage);

    }



}
