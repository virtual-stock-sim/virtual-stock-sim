package io.github.virtualstocksim.servlet;
import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Login Servlet: doGet");

        req.getRequestDispatcher("/_view/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("Login Servlet: doPost");

        // create account model
        Account acc = new Account();

        // store error message (if any)
        String errorMessage = null;

        // must create the controller each time, since it doesn't persist between POSTs
        AccountController controller = new AccountController();

        // assign model reference to allow controller to access it
        controller.setModel(acc);
        try {
            String uname = req.getParameter("uname");
            String pword = req.getParameter("pword");
            acc.setUname(uname);
            acc.setPword(pword);
            if(uname == null || uname.isEmpty() || pword == null || pword.isEmpty()) {
                errorMessage = "Login Failed. Please enter your username and password.";
                req.setAttribute("errorMessage", errorMessage);

            }
        } catch (NumberFormatException e){
            errorMessage = "Invalid String";
        }

        req.setAttribute("uname", req.getParameter("uname"));
        req.setAttribute("pword", req.getParameter("pword"));
        req.setAttribute("errorMessage", errorMessage);

        req.setAttribute("acc", acc);

        req.getRequestDispatcher("/_view/home.jsp").forward(req, resp);

    }

}