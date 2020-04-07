package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import io.github.virtualstocksim.transaction.TransactionHistory;
import io.github.virtualstocksim.transaction.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;

public class TransactionHistoryServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // check if session exists, if not the user is not logged in or timedout.
        HttpSession session = req.getSession(false);
        if (session == null) {
            logger.warn("Not logged in. Please login");
            resp.sendRedirect("/login");
        } else {
            logger.info("Transaction History Servlet Servlet: doGet");
            LinkedList<Transaction> transactions = new LinkedList<>();
            transactions.add(new Transaction(TransactionType.BUY, SQL.GetTimeStamp(), new BigDecimal("1252.2"), 2, Stock.Find(1).get()));
            transactions.add(new Transaction(TransactionType.BUY, SQL.GetTimeStamp(), new BigDecimal("50.12"), 3, Stock.Find(2).get()));
            transactions.add(new Transaction(TransactionType.SELL, SQL.GetTimeStamp(), new BigDecimal("500.7"), 100, Stock.Find(3).get()));
            transactions.add(new Transaction(TransactionType.BUY, SQL.GetTimeStamp(), new BigDecimal("123.8"), 4, Stock.Find(4).get()));
            transactions.add(new Transaction(TransactionType.SELL, SQL.GetTimeStamp(), new BigDecimal("65.2"), 120, Stock.Find(5).get()));
            TransactionHistory model = new TransactionHistory(transactions);
            req.setAttribute("model", model);
            req.getRequestDispatcher("/_view/transactionHistory.jsp").forward(req, resp);
        }
    }
}
