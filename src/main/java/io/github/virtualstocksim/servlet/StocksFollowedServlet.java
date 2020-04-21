package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.following.Follow;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

@WebServlet(urlPatterns = {"/stocksFollowed"})
public class StocksFollowedServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(StocksFollowedServlet.class);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("StocksFollowed Servlet: doGet");

        // check if session exists, if not the user is not logged in or timedout.
        HttpSession session = req.getSession(false);
        if (session == null) {
            logger.warn("Not logged in. Please login");
            resp.sendRedirect("/login");
            return;
        }
        Account localAcct = Account.Find(session.getAttribute("username").toString()).get();
        StocksFollowed model = new StocksFollowed(localAcct.getFollowedStocks());


        req.setAttribute("model", model);

        req.getRequestDispatcher("/_view/stocksFollowed.jsp").forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("StocksFollowed Servlet: doPost");
        // check if session exists, if not the user is not logged in or timedout.
        HttpSession session = req.getSession(false);
        if (session == null) {
            logger.warn("Not logged in. Please login");
            resp.sendRedirect("/login");
            return;
        }

        Account localAcct = Account.Find(session.getAttribute("username").toString()).get();
        AccountController localController = new AccountController();
        localController.setModel(localAcct);
        StocksFollowed model = new StocksFollowed(localAcct.getFollowedStocks());
        req.setAttribute("model", model);


        String sellShares = req.getParameter("shares-to-sell");
        String buyShares = req.getParameter("shares-to-buy");


        //We should add error checking here on MS4
        //This is probably very bad, especially if the forms persist & you change between buy and sell
        if(sellShares != null){
            try {
                localController.trade(TransactionType.SELL,"GOOGL",Integer.valueOf(sellShares));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(buyShares !=null){
            try {
                localController.trade(TransactionType.BUY,"GOOGL",Integer.valueOf(buyShares));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        req.getRequestDispatcher("/_view/stocksFollowed.jsp").forward(req, resp);

    }

}
