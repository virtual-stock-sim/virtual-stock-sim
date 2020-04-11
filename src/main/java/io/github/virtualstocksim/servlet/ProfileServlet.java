package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.encryption.Encryption;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.IOException;
import java.nio.file.Paths;

@MultipartConfig
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
        String errorMsg = null;

       if( Account.Find(session.getAttribute("username").toString()).isPresent() ) {
            Account acc = Account.Find(session.getAttribute("username").toString()).get();
            logger.info("Profile Servlet: doPost");

            //Update user bio
            if(req.getParameter("bio") !=null) {
                String bio = req.getParameter("bio"); // retrieve bio from form
                controller.updateUserBio(bio); // controller updates bio
                req.setAttribute("bio", bio);   // pass new bio back to display it in box
                req.getRequestDispatcher("/_view/profile.jsp").forward(req, resp);
                return;

            }
            // User is updating profile picture
            if (req.getPart("file")!=null ){
                String description = req.getParameter("description"); // Retrieves <input type="text" name="description">
                Part filePart = req.getPart("file"); // Retrieves <input type="file" name="file">
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
                controller.updateProfilePicture(filePart.getInputStream(),fileName); // controller updates profile picture


            }

            // User changing username
            if (req.getParameter("username")!=null ){

                String username = req.getParameter("username"); // retrieve username from form
                controller.updateUsername(username);    // controller updates username
                session.setAttribute("username", username); // bind new username to session (used for profile menu)
            }

            // User changing password
            if(req.getParameter("password")!=null) {
                // user changing password
                String password = req.getParameter("password"); // retrieve password from form
                if (password.length() >= 8) {    // check to ensure password meets required length
                    logger.info("Updating password to " + password); /*** THIS WILL NOT BE HERE IN THE FINAL PRODUCT, IT IS ONLY USED FOR TESTING ***/


                    controller.updatePassword(req.getParameter("password"));
                } else {
                    // else password did not meet required length, notify user
                    logger.info("Password must be at least 8 characters");
                    errorMsg = "Password must be at least 8 characters long.";
                    req.setAttribute("errorMsg", errorMsg);
                }
            }

    }else{
           logger.error("Error finding account with username "+session.getAttribute("username").toString());
           errorMsg="Whoops! something went wrong. Error: 404";
           req.setAttribute("errorMsg", errorMsg);
       }

            req.getRequestDispatcher("/_view/profile.jsp").forward(req, resp);

       }

    }
