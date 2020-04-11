package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

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
        // create account model

        //List<Transaction> transactions = new LinkedList<Transaction>();
        //TransactionHistory transactionHistory = new TransactionHistory(transactions);
        //List<Follow> stocks = new LinkedList<Follow>();
        //StocksFollowed stocksFollowed = new StocksFollowed(stocks);


        // store error message (if any)
        String errorMessage = null;

        // must create the controller each time, since it doesn't persist between POSTs
        AccountController controller = new AccountController();

        // assign model reference to allow controller to access it
        //controller.setModel(acc);

        try {
            String uname = req.getParameter("uname");
            String pword = req.getParameter("pword");


          if(!AccountController.login(uname,pword)){
            // If user input is invalid, return error message with same page
            errorMessage = "Login Failed. Please enter a valid username and password.";
            req.setAttribute("errorMessage", errorMessage);
            req.getRequestDispatcher("/_view/login.jsp").forward(req, resp);
            return;
          }
        } catch (NumberFormatException e){
            errorMessage = "Invalid credentials. Please enter a valid username and password";
            req.setAttribute("errorMessage", errorMessage);
            req.getRequestDispatcher("/_view/login.jsp").forward(req, resp);
            return;
        }



        // login is valid, redirect user
        // create session
        HttpSession session = req.getSession(true);
        String username = req.getParameter("uname");
        session.setAttribute("username", username);
        logger.info("Logging user" +username+ " in....");

        resp.sendRedirect("/home");

    }

}