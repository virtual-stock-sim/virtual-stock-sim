package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.account.AccountType;
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
//import org.apache.commons.validator.routines.EmailValidator;

@WebServlet(urlPatterns = {"/createAccount"})
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
            CreateAccountModel accountModel = new CreateAccountModel(email, uname);

            // check for fields containing values
            if((uname == null) || (pword == null) || (email == null) || (confirmPword == null))
            {
                errorMessage = "Required field(s) empty";
                req.setAttribute("errorMessage", errorMessage);
                req.setAttribute("CreateAccountModel", accountModel);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
                return;

                // check for passwords not matching
            }

            else if (!pword.equals(confirmPword))
            {
                errorMessage = "Passwords do not match. Please try again. ";
                req.setAttribute("errorMessage", errorMessage);
                req.setAttribute("CreateAccountModel", accountModel);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
                return;

                // check to make sure password is at least 8 characters
            }
            else if (pword.length() < 8)
            {
                errorMessage = "Passwords must be at least 8 characters long.";
                req.setAttribute("errorMessage", errorMessage);
                req.setAttribute("CreateAccountModel", accountModel);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
                return;
            }

            // check to make sure email is not taken
            else if (!Account.FindCustom("SELECT id FROM accounts WHERE email LIKE ?", "'%" + email + "%'").isEmpty())
            {
                // email exists
                errorMessage= "An account with this email already exists";
                req.setAttribute("errorMessage", errorMessage);
                req.setAttribute("CreateAccountModel", accountModel);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
                return;
            }
/*             commenting this out because it's preventing compilation
               I'll look into it later if i dont forget about it, but if it's working on other machines, i suspect something is weird with my pom.xml file or something
            // check to see if email is valid using regex
            else if(!EmailValidator.getInstance().isValid(accountModel.getEmail()))
            {
                errorMessage="Please enter a valid email.";
                req.setAttribute("errorMessage", errorMessage);
                req.setAttribute("CreateAccountModel", accountModel);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
                return;
            }
*/
            // check to see if username is taken
            else if (!Account.FindCustom("SELECT id FROM accounts WHERE username LIKE ?", "'%" + uname + "%'").isEmpty())
            {
                // username already exists
                errorMessage= "That username is already in use.";
                req.setAttribute("errorMessage", errorMessage);
                req.setAttribute("CreateAccountModel", accountModel);
                req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
                return;

            }
            // if none of these conditions are met, account can be created
            else
            {
               Optional<Account> account = Account.Create(accountModel.getUsername(), accountModel.getEmail(), pword, AccountType.USER);
               if(account.isPresent())
               {
                   AccountController controller = new AccountController();
                   controller.setModel(account.get());
               }

            }

        }
        catch (NumberFormatException e)
        {
            errorMessage = "Invalid credentials. Please enter a valid username and password.";
            req.setAttribute("errorMessage", errorMessage);
        }
        String uname = req.getParameter("uname");
        String pword = req.getParameter("pword");
        if(AccountController.login(uname,pword))
        {
            // login is valid, redirect user
            // create session
            HttpSession session = req.getSession(true);
            session.setAttribute("username", uname);
            logger.info("Logging user" +uname+ " in....");
            resp.sendRedirect("/home");
        }



    }

}
