package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@MultipartConfig
@WebServlet(urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Profile Servlet: doGet");

        /*// check if session exists, if not the user is not logged in or timedout.
        HttpSession session = req.getSession(false);
        if(session==null){
            logger.warn("Not logged in. Please login");
            resp.sendRedirect("/login");
        }else{
            Account acc = Account.Find(session.getAttribute("username").toString()).get();
            String bio = acc.getBio();
            req.setAttribute("bio", bio);
            logger.info("User "+acc.getUsername()+ " account settings");
            logger.info("Bio: "+bio);
            req.getRequestDispatcher("/_view/profile.jsp").forward(req, resp);
        }

        */

        String errorMsg = null;

        HttpSession session = req.getSession(false);
        if(session == null)
        {
            logger.info("User isn't logged in, redirecting to login page");
            resp.sendRedirect("/login");
        }
        else
        {
            // Make sure user is logged in
            String sessionUsername = session.getAttribute("username").toString();
            Optional<Account> account;
            if(sessionUsername != null && !sessionUsername.isEmpty())
            {
                account = Account.Find(sessionUsername);
            }
            else
            {
                account = Optional.empty();
            }

            if(account.isPresent())
            {
                req.setAttribute("account", account.get());
            }
            else
            {
                req.setAttribute("errorMsg", "Whoops! Something went wrong on our end");
            }

            req.getRequestDispatcher("/_view/profile.jsp").forward(req, resp);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Profile Servlet: doPost");

        AccountController controller = new AccountController();
        HttpSession session = req.getSession(false);
        String lastError;
        List<String> errorMsgs = new LinkedList<>();
        boolean bioUpdateSuccess;
        boolean pictureUpdateSuccess;
        boolean credentialUpdateSuccess;


        // Make sure user is logged in
        String sessionUsername = session.getAttribute("username").toString();
        Optional<Account> account;
        if(sessionUsername != null && !sessionUsername.isEmpty())
        {
            account = Account.Find(sessionUsername);
        }
        else
        {
            account = Optional.empty();
        }

        if(account.isPresent())
        {
            controller.setModel(account.get());

            // Update user bio
            String bio = req.getParameter("bio");
            if(bio != null)
            {
                logger.info("User requested bio change");
                controller.updateUserBio(bio);
                bioUpdateSuccess = true;
                req.setAttribute("bioUpdateSuccess", bioUpdateSuccess);

            }
            /* TODO: Look into a better way to check for the picture field being null**/
            /* TODO: Success message */
            // User is updating profile picture
            if (!req.getParts().isEmpty())
            {
                Part profilePic = req.getPart("file");
                if (profilePic != null)
                {
                    logger.info("User requested profile picture change");
                    // Make sure file is an image
                    if (!profilePic.getContentType().contains("image"))
                    {
                        lastError = "Uploaded file isn't an image";
                        errorMsgs.add(lastError);
                        logger.info(lastError);
                    }
                    // Make sure file doesn't exceed file size limit
                    else if (profilePic.getSize() >= Account.ProfilePictureMaxFileSize())
                    {
                        lastError = "Uploaded file exceeds file size limit";
                        errorMsgs.add(lastError);
                        logger.info(lastError);
                    } else {
                        try
                        {
                            controller.updateProfilePicture(profilePic.getInputStream(), Paths.get(profilePic.getSubmittedFileName()).getFileName().toString());
                        }
                        catch (SQLException e)
                        {
                            logger.error("", e);
                            errorMsgs.add("Server-side error");
                        }
                    }
                }

            }

             // User changing username
            String username = req.getParameter("username");
            if(username != null)
            {
                logger.info("User requested username change");
                if(!Account.FindCustom("SELECT id FROM account WHERE username LIKE ?", "'%" + username + "%'").isEmpty())
                {
                    lastError = "Username already exists";
                    logger.warn(lastError);
                    errorMsgs.add(lastError);
                }
                else
                {
                    controller.updateUsername(username);
                }
            }

            String password = req.getParameter("password");
            if(password != null)
            {
                logger.info("User requested password change");

                if(password.length() < 8)
                {
                    lastError = "Password must be at least 8 characters long";
                    logger.warn(lastError);
                    errorMsgs.add(lastError);
                }
                else
                {
                    controller.updatePassword(password);
                }
            }

        }
        else
        {
            logger.error("Error finding account with username " + sessionUsername);
            errorMsgs.add("Whoops! Something went wrong on our end");
        }

        if(!errorMsgs.isEmpty())
        {
            req.setAttribute("errorMsg", String.join("<br>"));
        }

        req.setAttribute("account", controller.getModel());
        req.getRequestDispatcher("/_view/profile.jsp").forward(req, resp);

       }

}
