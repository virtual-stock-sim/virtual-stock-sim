package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.account.CreateAccountModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Login Servlet: doGet");

        // do not create a new session until the user logs in
        HttpSession session = req.getSession(false);

        req.getRequestDispatcher("/_view/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("Login Servlet: doPost");
        // store error message (if any)
        String errorMessage = null;

        String uname = req.getParameter("uname");
        String pword = req.getParameter("pword");

        if(uname == null || pword == null)
        {
            resp.sendRedirect("/500");
            return;
        }

        AccountController controller = new AccountController();
        Optional<Account> account = Account.Find(uname);
        account.ifPresent(controller::setModel);

        if(account.isPresent() && controller.login(uname.trim(),pword.trim()))
        {
            // login is valid, redirect user and create session
            HttpSession session = req.getSession(true);
            session.setAttribute("uuid", controller.getModel().getUUID());
            logger.info("Logging user" + uname + " in....");

            resp.sendRedirect("/home");
        }
        else
        {
            // If user input is invalid, return error message with same page
            errorMessage = "Login Failed. That username and password combination is not in our system";

            // Make the username persist in the form
            req.setAttribute("errorMessage", errorMessage);
            req.setAttribute("uname", uname);
            req.getRequestDispatcher("/_view/login.jsp").forward(req, resp);
        }
    }

}