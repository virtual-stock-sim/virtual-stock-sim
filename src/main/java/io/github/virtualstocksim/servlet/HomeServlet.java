package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.account.CreateAccountModel;
import io.github.virtualstocksim.leaderboard.LeaderBoard;
import io.github.virtualstocksim.leaderboard.TopStocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Home Servlet: doGet");
        LeaderBoard lb = new LeaderBoard();
        try {
            lb.updateRanks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        lb.getCurrentRanks();

        Optional<Account> account = SessionValidater.validate(req);

        AccountController controller = new AccountController();
        account.ifPresent(controller::setModel);
        LeaderBoard leaderBoard = new LeaderBoard();
        TopStocks topStocks = new TopStocks();
        try {
            //this update ranks function is what should be called by a task scheduler
            //leaving this here now to show that it has functionality
            leaderBoard.updateRanks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        req.setAttribute("topStocksModel",topStocks);
        req.setAttribute("leaderboardModel",leaderBoard);

        req.getRequestDispatcher("/_view/home.jsp").forward(req, resp);
    }
}