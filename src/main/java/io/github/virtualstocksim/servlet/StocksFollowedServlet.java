package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.database.SQL;
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

        ArrayList<Follow> followingList= new ArrayList<>();
        followingList.add(new Follow(new BigDecimal(100), Stock.Find(1).get(), SQL.GetTimeStamp()));
        followingList.add(new Follow(new BigDecimal(498), Stock.Find(2).get(),SQL.GetTimeStamp()));
        followingList.add(new Follow(new BigDecimal(320), Stock.Find(3).get(),SQL.GetTimeStamp()));
        followingList.add(new Follow(new BigDecimal(5), Stock.Find(4).get(),SQL.GetTimeStamp()));
        followingList.add(new Follow(new BigDecimal(.12), Stock.Find(5).get(),SQL.GetTimeStamp()));

        StocksFollowed model = new StocksFollowed(followingList);
        req.setAttribute("model",model);

        req.getRequestDispatcher("/_view/stocksFollowed.jsp").forward(req, resp);
    }
}
