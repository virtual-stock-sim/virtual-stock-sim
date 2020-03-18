package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.account.AccountType;
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
import java.util.LinkedList;
import java.util.List;

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
        byte[] bytes = {3,4,5,6,7,8};
        LinkedList<Stock> stocksFollowed = new LinkedList<Stock>();
        List<Transaction> transactions = new LinkedList<Transaction>();
        TransactionHistory transactionHistory = new TransactionHistory(transactions);

        Account acc = new Account(0, "371298372189", AccountType.ADMIN, "VSSAdmin",bytes,bytes,
                stocksFollowed, transactionHistory,-1,"","");

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


            // If user input is invalid, return error message with same page
            if(!loginIsValid(uname, pword)) {
                errorMessage = "Login Failed. Please enter a valid username and password.";
                req.setAttribute("errorMessage", errorMessage);
                req.getRequestDispatcher("/_view/login.jsp").forward(req, resp);
                /*
                 * QUIT EARLY
                 */
                return;
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