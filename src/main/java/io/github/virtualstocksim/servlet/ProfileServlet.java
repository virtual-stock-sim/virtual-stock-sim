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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.zip.DataFormatException;
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
        Account acc = Account.Find(session.getAttribute("username").toString()).get();
        logger.info("Profile Servlet: doPost");
        String errorMsg = null;

            //Update user bio
            if(req.getParameter("bio") !=null) {
                String bio = req.getParameter("bio"); // retrieve bio from form
                controller.updateUserBio(acc.getId(), bio); // controller updates bio
                req.setAttribute("bio", bio);   // pass new bio back to display it in box
                req.getRequestDispatcher("/_view/profile.jsp").forward(req, resp);
                return;

            }
            // User is updating profile picture
            if (req.getPart("file")!=null ){
                String description = req.getParameter("description"); // Retrieves <input type="text" name="description">
                Part filePart = req.getPart("file"); // Retrieves <input type="file" name="file">
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
                InputStream fileContent = filePart.getInputStream(); // convert to image stream
                File uploadDir = new File("./userdata/ProfilePictures"); // directory where images are stored
                if(!uploadDir.exists()){
                    uploadDir.mkdirs();
                }
                File file = File.createTempFile(fileName,".tmp", uploadDir); // write file to directory
                logger.info(file.toString());
                Files.copy(fileContent, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                controller.updateProfilePicture(acc.getId(),file.toString());
                logger.info("Profile Picture successfully updated to " +file.toString());


            }
                // User changing username
            if (req.getParameter("username")!=null ){

                String username = req.getParameter("username"); // retrieve username from form
                controller.updateUsername(acc.getId(),username);    // controller updates username
                session.setAttribute("username", username); // bind new username to session (used for profile menu)
            }
            if(req.getParameter("password")!=null) {
                // user changing password
                String password = req.getParameter("password"); // retrieve password from form
                if(password.length() >= 8) {    // check to ensure password meets required length
                    logger.info("Updating password to " + password); /*** THIS WILL NOT BE HERE IN THE FINAL PRODUCT, IT IS ONLY USED FOR TESTING ***/

                    // Encrypt new password, and store it in DB
                    byte[] newSalt = Encryption.getNextSalt();
                    byte[] newHash = Encryption.hash(password.toCharArray(), newSalt);
                    controller.updatePassword(acc.getId(), newHash, newSalt);
                }else{
                    // else password did not meet required length, notify user
                    logger.info("Password must be at least 8 characters");
                    errorMsg ="Password must be at least 8 characters long.";
                    req.setAttribute("errorMsg", errorMsg);
                }
            }

            req.getRequestDispatcher("/_view/profile.jsp").forward(req, resp);




    }

}
