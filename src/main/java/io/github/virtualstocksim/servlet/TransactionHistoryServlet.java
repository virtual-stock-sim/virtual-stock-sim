package io.github.virtualstocksim.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import io.github.virtualstocksim.model.TransactionHistory;

public class TransactionHistoryServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Home Servlet: doGet");
        TransactionHistory model = new TransactionHistory();
        req.setAttribute("model",model);
        req.setAttribute("stockList",model.getTransactions());
        req.getRequestDispatcher("/_view/transactionHistory.jsp").forward(req, resp);
    }
}
