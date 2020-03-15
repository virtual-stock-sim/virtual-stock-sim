package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.following.Follow;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

public class StocksFollowedServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(StocksFollowedServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("StocksFollowed Servlet: doGet");

        ArrayList<Follow> follwingList= new ArrayList<>();
        follwingList.add(new Follow(new BigDecimal(100), Stock.GetStock(1).get()));
        follwingList.add(new Follow(new BigDecimal(498), Stock.GetStock(2).get()));
        follwingList.add(new Follow(new BigDecimal(320), Stock.GetStock(3).get()));
        follwingList.add(new Follow(new BigDecimal(5), Stock.GetStock(4).get()));
        follwingList.add(new Follow(new BigDecimal(.12), Stock.GetStock(5).get()));
        StocksFollowed model = new StocksFollowed(follwingList);
        req.setAttribute("model",model);

        req.getRequestDispatcher("/_view/stocksFollowed.jsp").forward(req, resp);
    }
}
