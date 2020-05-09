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
import java.util.Optional;

@WebServlet(urlPatterns = {"/transactionHistory"})
public class TransactionHistoryServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Transaction History Servlet Servlet: doGet");

        Account account = SessionValidater.validate(req).orElse(null);

        if (account == null) {
            logger.warn("Not logged in. Please login");
            resp.sendRedirect("/login");
            return;
        }

        TransactionHistory model = new TransactionHistory(account.getTransactionHistory());
        req.setAttribute("model", model);
        req.setAttribute("account", account);
        req.getRequestDispatcher("/_view/transactionHistory.jsp").forward(req, resp);
    }
}
