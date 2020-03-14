package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;

import io.github.virtualstocksim.model.TransactionHistory;

public class TransactionHistoryServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Home Servlet: doGet");
        LinkedList<Transaction> transactions = new LinkedList<>();
        transactions.add(new Transaction(Transaction.TransactionType.BUY,"3/13/20",new BigDecimal("12.2"),12, Stock.GetStock(1).get()));
        //transactions.add();
        //transactions.add();
        TransactionHistory model = new TransactionHistory(transactions);
        req.setAttribute("model",model);
        req.setAttribute("stockList",model.getTransactions());
        req.getRequestDispatcher("/_view/transactionHistory.jsp").forward(req, resp);
    }
}
