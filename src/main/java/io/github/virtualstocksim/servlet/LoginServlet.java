package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.account.AccountType;
import io.github.virtualstocksim.following.Follow;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class LoginServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);

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


          Optional<Account> acc = AccountController.login(uname,pword);

          if( !acc.isPresent() || !loginIsValid(uname,pword) ){
            // If user input is invalid, return error message with same page
            errorMessage = "Login Failed. Please enter a valid username and password.";
            req.setAttribute("errorMessage", errorMessage);
            req.getRequestDispatcher("/_view/login.jsp").forward(req, resp);
            return;
          }
        } catch (NumberFormatException e){
            errorMessage = "Invalid credentials. Please enter a valid username and password";
        }

        req.setAttribute("errorMessage", errorMessage);


        // login is valid, redirect user
        req.getRequestDispatcher("/_view/home.jsp").forward(req, resp);

    }

    /*
     * TODO: Make a function to validate uname and pword (i.e. that the input is of the correct format,
     *  isn't empty, isn't just a bunch of spaces, etc) instead of just using *.isEmpty();
     */
    public boolean loginIsValid(String username, String password){
        if(username.isEmpty() || password.isEmpty() || (username.getClass() != String.class)
                || password.getClass() != String.class || password.contains(" ") || username.contains(" ") ) {
            return false;
        }
        return true;
    }

}