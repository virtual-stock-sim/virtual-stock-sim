package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.following.Follow;
import io.github.virtualstocksim.following.StocksFollowed;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Investment;
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

        Optional<Account> account = SessionValidater.validate(req);

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

            String stocksInPage = (String) req.getAttribute("stocks-in-page");
            if (!stocksInPage.isEmpty()) {
                String[] symbolList = stocksInPage.split(",");
               List<Stock> stockList = new ArrayList<Stock>(symbolList.length);


                for (String symbol : symbolList) {
                    stockList.add(Stock.Find(symbol).orElse(null));
                }


                List<Follow> stocksFollowedInPage = new ArrayList<Follow>();
                List<Investment> stocksInvestedInPage = new ArrayList<Investment>();
                for (Stock stock : stockList) {
                    if()
                }
            }
        }
    }
}
