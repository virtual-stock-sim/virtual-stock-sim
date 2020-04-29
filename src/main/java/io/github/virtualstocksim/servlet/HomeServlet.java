package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.account.CreateAccountModel;
import io.github.virtualstocksim.leaderboard.LeaderBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Home Servlet: doGet");

        // do not create a new session until the user logs in
        HttpSession session = req.getSession(false);
        if(session!=null)
        {
            String username = (String) session.getAttribute("username");
            CreateAccountModel accountModel = new CreateAccountModel(username);
            Account acc = Account.Find(username).orElse(null);
            AccountController controller = new AccountController();
            controller.setModel(acc);
            LeaderBoard leaderBoard = new LeaderBoard();
            req.setAttribute("model",leaderBoard);
            if(controller.getModel() !=null)
            {
                req.setAttribute("CreateAccountModel", accountModel);
                req.setAttribute("account", acc);
                if(controller.getModel().getProfilePicture().length() == 0)
                {
                    // if the user has not uploaded a profile picture, default it to question mark
                    req.setAttribute("picturepath", "../_view/resources/images/home/question-mark.jpg");

                    logger.info("profile picture was null - defaulted to Question Mark");
                }
                else
                {
                    req.setAttribute("picturepath", controller.getModel().getProfilePictureWithDir());
                }
                req.setAttribute("username", username);
                logger.info(username + " is logged in");

            }
        } else logger.info("Session was null - user not logged in");

        req.getRequestDispatcher("/_view/home.jsp").forward(req, resp);
    }
}