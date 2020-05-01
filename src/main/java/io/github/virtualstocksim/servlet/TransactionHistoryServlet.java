package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.transaction.TransactionHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/transactionHistory"})
public class TransactionHistoryServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Transaction History Servlet Servlet: doGet");
        // check if session exists, if not the user is not logged in or timedout.
        HttpSession session = req.getSession(false);

        if (session == null) {
            logger.warn("Not logged in. Please login");
            resp.sendRedirect("/login");
            return;
        }
        else
        {
            String username = session.getAttribute("username").toString();
            Account localAcct = Account.Find(username).orElse(null);
            if(localAcct !=null)
            {
                TransactionHistory model = new TransactionHistory(localAcct.getTransactionHistory());
                req.setAttribute("model", model);
                req.setAttribute("account", localAcct);
                logger.info("LOOK HERE BRETT " + localAcct.getTransactionHistory());
                req.getRequestDispatcher("/_view/transactionHistory.jsp").forward(req, resp);
            }
            else
            {
                logger.error("Account not found");
            }

        }
    }
}
