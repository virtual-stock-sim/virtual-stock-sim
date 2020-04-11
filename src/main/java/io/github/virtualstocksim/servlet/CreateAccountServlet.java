package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.account.AccountType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
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
            String confirmPword = req.getParameter("pwordconfirm");

            // check for fields containing values
            if((uname == null) || (pword == null) || (email == null) || (confirmPword == null)){
                errorMessage = "Required field(s) empty";
                req.setAttribute("errorMessage", errorMessage);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
                return;

                // check for passwords not matching
            } else if (!pword.equals(confirmPword)){
                errorMessage = "Passwords do not match. Please try again. ";
                req.setAttribute("errorMessage", errorMessage);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
                return;

                // check to make sure password is at least 8 characters
            }else if(pword.length() < 8){
                errorMessage = "Passwords must be at least 8 characters long.";
                req.setAttribute("errorMessage", errorMessage);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
                return;

                // check to make sure email is not taken
            } else if(!Account.FindCustom("SELECT id FROM accounts WHERE email LIKE ?", "'%" + email + "%'").isEmpty()){
                // email exists
                errorMessage= "An account with this email already exists";
                req.setAttribute("errorMessage", errorMessage);
                req.setAttribute("email", email);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
                return;

                // check to see if username is taken
            } else if(!Account.FindCustom("SELECT id FROM accounts WHERE username LIKE ?", "'%" + uname + "%'").isEmpty()){
                // username already exists
                errorMessage= "That username is already in use.";
                req.setAttribute("errorMessage", errorMessage);
                req.setAttribute("uname", uname);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
                return;

            // if none of these conditions are met, account can be created
            } else {
                Account.Create(uname, email, pword, AccountType.ADMIN);
            }

        } catch (NumberFormatException e){
            errorMessage = "Invalid credentials. Please enter a valid username and password.";
            req.setAttribute("errorMessage", errorMessage);
        }
        String uname = req.getParameter("uname");
        String pword = req.getParameter("pword");
        Optional<Account> acc = AccountController.login(uname,pword);
        // login is valid, redirect user
        // create session
        HttpSession session = req.getSession(true);
        session.setAttribute("username", uname);
        logger.info("Logging user" +uname+ " in....");

        resp.sendRedirect("/home");


    }

}
