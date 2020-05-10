package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.following.FollowedStock;
import io.github.virtualstocksim.following.FollowedStocks;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Investment;
import io.github.virtualstocksim.transaction.InvestmentCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/compare"})
public class CompareServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(CompareServlet.class);

    @Override
    public void init() throws ServletException
    {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Compare Servlet: doGet");

        // check if session exists, if not the user is not logged in or timeout.
        Account account = SessionValidater.validate(req).orElse(null);
        if (account == null) {
            logger.warn("Not logged in. Please login");
            resp.sendRedirect("/login");
            return;
        }

        req.getRequestDispatcher("/_view/compare.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Compare Servlet: doPost");

        // check if session exists, if not the user is not logged in or timed out.
        Account account = SessionValidater.validate(req).orElse(null);
        if(account!=null) {
            AccountController controller = new AccountController();
            controller.setModel(account);

            String stocksInPage = req.getParameter("stocks-in-page");
            if (stocksInPage!=null)
            {
                // parse out each of the symbols from list
                logger.info("Stock in page: "+ stocksInPage);
                String[] symbolList = stocksInPage.split(",");


                InvestmentCollection investedStocks = new InvestmentCollection(controller.getModel().getInvestedStocks());
                List<Investment> stocksInvestedInPage = new ArrayList<>();
                List<FollowedStock> stocksFollowedInPage = new ArrayList<>();
                List<Stock> notFollowedOrInvested = new ArrayList<>();


               // determine if user is following, invested, or neither in each stock in the list
                for (String symbol : symbolList)
                {
                    if(controller.isFollowingStock(symbol))
                    {
                        Optional<FollowedStock> followedStock = controller.getFollowedStock(symbol);
                        if(followedStock.isPresent())
                        {
                            stocksFollowedInPage.add(followedStock.get());
                        }
                    }
                    else if(investedStocks.isInvested(symbol))
                    {
                        stocksInvestedInPage.add(investedStocks.getInvestment(symbol));
                    }
                    else
                    {
                        Optional<Stock> stock = Stock.Find(symbol);
                        if(stock.isPresent())
                        {
                            notFollowedOrInvested.add(stock.get());
                        }
                    }
                }

                logger.info("Invested List Size: " + stocksInvestedInPage.size());
                logger.info("Followed List Size: " + stocksFollowedInPage.size());
                logger.info("Neither List Size: " + notFollowedOrInvested.size());

                req.setAttribute("investedList", stocksInvestedInPage);
                req.setAttribute("followedList", stocksFollowedInPage);
                req.setAttribute("notFollowedOrInvestedList", notFollowedOrInvested);
                req.setAttribute("stocksSearched", stocksInPage);

            }
            else{
                logger.error("STOCKS IN PAGE WAS NULL");
            }
        }

        req.getRequestDispatcher("/_view/compare.jsp").forward(req, resp);
    }
}
