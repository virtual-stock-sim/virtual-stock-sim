package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.encryption.Encryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.zip.DataFormatException;

public class ProfileServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Profile Servlet: doGet");

        // check if session exists, if not the user is not logged in or timedout.
        HttpSession session = req.getSession(false);
        if(session==null){
            logger.warn("Not logged in. Please login");
            resp.sendRedirect("/login");
        }else{
            Account acc = Account.Find(session.getAttribute("username").toString()).get();
            String bio = acc.getBio();
            req.setAttribute("bio", bio);
            logger.info("User "+acc.getUname()+ " account settings");
            logger.info("Bio: "+bio);
            req.getRequestDispatcher("/_view/profile.jsp").forward(req, resp);
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AccountController controller = new AccountController();
        HttpSession session = req.getSession(false);
        Account acc = Account.Find(session.getAttribute("username").toString()).get();
        logger.info("Profile Servlet: doPost");
        String errorMsg = null;

            //Update user bio
            if(req.getParameter("bio") !=null) {
                String bio = req.getParameter("bio");
                controller.updateUserBio(acc.getId(), bio);
                req.setAttribute("bio", bio);
                req.getRequestDispatcher("/_view/profile.jsp").forward(req, resp);
                return;

            }
            if (req.getParameter("profilePic")!=null) {
                // user is editing profile picture

            }

            if (req.getParameter("username")!=null ){
                // User changes username
                String username = req.getParameter("username");
                controller.updateUsername(acc.getId(),username);
                session.setAttribute("username", username);
            }
            if(req.getParameter("password")!=null) {
                // user changing password
                String password = req.getParameter("password");
                if(password.length() >= 8) {
                    logger.info("Updating password to " + password);
                    byte[] newSalt = Encryption.getNextSalt();
                    byte[] newHash = Encryption.hash(password.toCharArray(), newSalt);
                    controller.updatePassword(acc.getId(), newHash, newSalt);
                }else{
                    logger.info("Password must be at least 8 characters");
                    errorMsg ="Password must be at least 8 characters long.";
                    req.setAttribute("errorMsg", errorMsg);
                }
            }

            req.getRequestDispatcher("/_view/profile.jsp").forward(req, resp);




    }

}
