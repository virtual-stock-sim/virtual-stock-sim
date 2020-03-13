package io.github.virtualstocksim.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import io.github.virtualstocksim.model.transactionHistoryModel;

public class stocksFollowedServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("StocksFollowed Servlet: doGet");
        transactionHistoryModel model = new transactionHistoryModel();
        req.setAttribute("model",model);
        req.getRequestDispatcher("/_view/stocksFollowed.jsp").forward(req, resp);
    }
}
