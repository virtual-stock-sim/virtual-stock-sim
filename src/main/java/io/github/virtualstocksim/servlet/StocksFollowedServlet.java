
package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.following.FollowedStocks;
import io.github.virtualstocksim.transaction.InvestmentCollection;
import io.github.virtualstocksim.transaction.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/stocksFollowed"})
public class StocksFollowedServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(StocksFollowedServlet.class);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("StocksFollowed Servlet: doGet");

        // check if session exists, if not the user is not logged in or timeout.
        Account account = SessionValidater.validate(req).orElse(null);
        if (account == null) {
            logger.warn("Not logged in. Please login");
            resp.sendRedirect("/login");
            return;
        }

        FollowedStocks followedModel = new FollowedStocks(account.getFollowedStocks());
        InvestmentCollection investModel = new InvestmentCollection(account.getInvestedStocks());
        req.setAttribute("followedModel", followedModel);
        req.setAttribute("investModel", investModel);
        req.getRequestDispatcher("/_view/stocksFollowed.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("StocksFollowed Servlet: doPost");

        String errorMsg = null;
        String buySuccessMsg = null;
        String sellSuccessMsg = null;
        String stockUnfollowSuccess = null;

        // check if session exists, if not the user is not logged in or timed out.
        Account account = SessionValidater.validate(req).orElse(null);
        if (account != null) {

            AccountController accountController = new AccountController();
            accountController.setModel(account);

            FollowedStocks followedModel = new FollowedStocks(account.getFollowedStocks());
            InvestmentCollection investModel = new InvestmentCollection(account.getInvestedStocks());

            req.setAttribute("followedModel", followedModel);
            req.setAttribute("investModel", investModel);
            req.setAttribute("account", account);

            String sellShares = req.getParameter("shares-to-sell");
            String buyShares = req.getParameter("shares-to-buy");
            String stockName = req.getParameter("stock-name");
            String stockToUnfollow = req.getParameter("stock-to-unfollow");



            //We should add error checking here on MS4
            //This is probably very bad, especially if the forms persist & you change between buy and sell
            if (sellShares != null) {
                try {
                    accountController.trade(TransactionType.SELL, stockName, Integer.parseInt(sellShares.trim()));
                    sellSuccessMsg="You have successfully sold "+Integer.valueOf(sellShares)+" shares of "+stockName+" stock.";
                    req.setAttribute("sellSuccessMsg", sellSuccessMsg);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (buyShares != null)
            {
                try
                {
                    accountController.trade(TransactionType.BUY, stockName, Integer.parseInt(buyShares.trim()));
                    buySuccessMsg = "You have successfully purchased " + Integer.valueOf(
                            buyShares) + " shares of " + stockName + " stock.";
                    req.setAttribute("buySuccessMsg", buySuccessMsg);
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }

            if(stockToUnfollow!=null){
                try {
                    accountController.unfollowStock(stockToUnfollow);
                    accountController.unInvest(stockToUnfollow);
                    stockUnfollowSuccess= "You have unfollowed "+stockToUnfollow+ " and your remaining shares were sold.";
                    req.setAttribute("stockUnfollowSuccess", stockUnfollowSuccess);
                } catch (SQLException e){
                    logger.info("Error unfollowing "+stockToUnfollow+ ":"+e);
                }

            }
        }


        req.getRequestDispatcher("/_view/stocksFollowed.jsp").forward(req, resp);

    }

}
