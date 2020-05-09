package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
    }
}
