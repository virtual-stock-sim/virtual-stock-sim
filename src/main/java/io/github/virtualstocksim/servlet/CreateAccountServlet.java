package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class CreateAccountServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CreateAccountServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Create Account Servlet: doGet");

        req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("Create Account Servlet: doPost");

        // String to hold error message (if any)
        String errorMessage = null;

        try {

            String uname = req.getParameter("uname");
            String pword = req.getParameter("pword");
            String email = req.getParameter("email");
            String confirmPword = req.getParameter("pwordConfirm");

            // check for fields containing values
            if((uname == null) || (pword == null) || (email == null) || (confirmPword == null) || uname.isEmpty()){
                errorMessage = "Required field(s) empty ";
                req.setAttribute("errorMessage", errorMessage);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
            }

            // check to make sure passwords match
            if(pword != confirmPword){
                errorMessage = "Passwords do not match. Please try again. ";
                req.setAttribute("errorMessage", errorMessage);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
            }
            // check to make sure password meets length requirements
            if(pword.length() < 8 || pword.isEmpty() || confirmPword.isEmpty()){
                errorMessage = "Passwords must be at least 8 characters long.";
                req.setAttribute("errorMessage", errorMessage);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
            }

            Account.Create(uname,email,pword,"ADMIN");

        }catch (NumberFormatException e){
            errorMessage = "Invalid credentials. Please enter a valid username and password.";
        }

        req.setAttribute("errorMessage", errorMessage);


    }

}
