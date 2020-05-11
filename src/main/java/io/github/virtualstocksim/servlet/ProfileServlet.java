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
public class ProfileServlet extends HttpServlet
{

    private static final Logger logger = LoggerFactory.getLogger(ProfileServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Profile Servlet: doGet");

        String errorMsg = null;

        HttpSession session = req.getSession(false);
        if (session == null)
        {
            logger.info("User isn't logged in, redirecting to login page");
            resp.sendRedirect("/login");
        }
        else
        {
            // Make sure user is logged in
            Optional<Account> account = SessionValidater.validate(req);

            if (account.isPresent())
            {
                req.setAttribute("account", account.get());
            }
            else
            {
                errorMsg = "Whoops! Something went wrong on our end";
                req.setAttribute("errorMsg", errorMsg);
            }

            req.getRequestDispatcher("/_view/profile.jsp").forward(req, resp);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Profile Servlet: doPost");

        AccountController controller = new AccountController();
        String lastError;
        List<String> errorMsgs = new LinkedList<>();
        boolean bioUpdateSuccess;
        boolean pictureUpdateSuccess;
        boolean credentialUpdateSuccess;
        boolean resetTransHistSuccess, resetFollowedSuccess;
        boolean optOutSuccess, optInSuccess;


        Optional<Account> account = SessionValidater.validate(req);

        if (account.isPresent())
        {
            controller.setModel(account.get());

            // Update user bio
            String bio = req.getParameter("bio");
            if (bio != null)
            {
                if (bio.length() > 500)
                {
                    logger.warn("Bio is greater than 500 characters. Abandoning update...");
                    errorMsgs.add("Bio is greater than 500 characters!");
                }
                else
                {
                    logger.info("User requested bio change");
                    controller.updateUserBio(bio);
                    bioUpdateSuccess = true;
                    req.setAttribute("bioUpdateSuccess", bioUpdateSuccess);
                }

            }
            /* TODO: Look into a better way to check for the picture field being null**/
            // User is updating profile picture
            if (req.getContentType().contains("multipart/form-data"))
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
                    }
                    else
                    {
                        try
                        {
                            controller.updateProfilePicture(
                                    profilePic.getInputStream(), Paths.get(
                                            profilePic.getSubmittedFileName()).getFileName().toString());
                            pictureUpdateSuccess = true;
                            req.setAttribute("pictureUpdateSuccess", pictureUpdateSuccess);
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
            if (username != null)
            {
                username = username.trim();
                if (username.length() > 255)
                {
                    errorMsgs.add("Username cannot exceed 255 characters");
                    logger.warn("Username cannot exceed 255 characters, abandoning update....");

                }
                else
                {

                    logger.info("User requested username change");
                    if (!Account.FindCustom("SELECT id FROM account WHERE LOWER(username) = LOWER(?)", username).isEmpty())
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
            }

            String password = req.getParameter("password");
            String confirmPassword = req.getParameter("confirmPassword");
            if (password != null && confirmPassword != null && !(password = password.trim()).isEmpty() && !(confirmPassword = confirmPassword.trim()).isEmpty())
            {
                if (password.length() > 255)
                {
                    errorMsgs.add("Password cannot exceed 255 characters");
                    logger.warn("Password cannot exceed 255 characters. Abandoning update...");
                }
                else
                {
                    logger.info("User requested password change");

                    if (password.length() < 8)
                    {
                        lastError = "Password must be at least 8 characters long";
                        logger.warn(lastError);
                        errorMsgs.add(lastError);
                    }
                    // check for matching passwords
                    else if (!confirmPassword.equals(password))
                    {
                        errorMsgs.add("Password do not match");
                    }
                    else
                    // else data is good, do update
                    {
                        controller.updatePassword(password);
                        credentialUpdateSuccess = true;
                        req.setAttribute("credentialUpdateSuccess", credentialUpdateSuccess);
                    }
                }
            }
            // Only give an error if both fields are empty (as in they aren't changing their password)
            else if ((password != null && confirmPassword == null) || (password == null && confirmPassword != null))
            // if password or confirm password is null, notify user
            {
                errorMsgs.add("Required field empty. Be sure to confirm your password!");
            }

            // check for user changing email
            String newEmail = req.getParameter("new-email");
            if (newEmail != null && !newEmail.isEmpty())
            {
                newEmail = newEmail.trim();
                if (!Account.FindCustom("SELECT id FROM account WHERE LOWER(email) = LOWER(?)", newEmail).isEmpty())
                {
                    // email exists
                    errorMsgs.add("An account with this email already exists");
                }
                else
                {
                    controller.resetEmail(newEmail);
                    credentialUpdateSuccess = true;
                    req.setAttribute("credentialUpdateSuccess", credentialUpdateSuccess);
                    logger.info("Email successfully updated in database");
                }
            }

            // check for user resetting either transaction history or followed stocks
            String resetTransHist = req.getParameter("reset-transaction-history");
            String resetFollowed = req.getParameter("reset-followed");
            if (resetTransHist != null)
            {
                controller.resetTransactionHistory();
                resetTransHistSuccess = true;
                req.setAttribute("resetTransHistSuccess", resetTransHistSuccess);
                logger.info("Transaction History successfully cleared");
            }
            if (resetFollowed != null)
            {
                controller.resetFollowed();
                resetFollowedSuccess = true;
                req.setAttribute("resetFollowedSuccess", resetFollowedSuccess);
                logger.info("Followed Stocks successfully cleared");
            }

            String optIn = req.getParameter("leaderboard-opt-in");
            if (optIn != null && optIn.equals("in"))
            {
                controller.optInToLeaderboard();
                optInSuccess = true;
                req.setAttribute("optInSuccess", optInSuccess);
                logger.info("User successfully opted into leaderboard");

            }
            else if (optIn != null && optIn.equals("out"))
            {
                controller.optOutOfLeaderboard();
                optOutSuccess = true;
                req.setAttribute("optOutSuccess", optOutSuccess);
                logger.info("User successfully opted out of leaderboard");
            }


            if (!errorMsgs.isEmpty())
            {
                req.setAttribute("errorMsgs", errorMsgs);
            }

            req.setAttribute("account", controller.getModel());
            req.getRequestDispatcher("/_view/profile.jsp").forward(req, resp);
        }
        else
        {
            resp.sendRedirect("/login");
        }
    }

}
