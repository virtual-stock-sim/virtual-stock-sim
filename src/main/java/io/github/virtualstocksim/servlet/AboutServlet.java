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

@WebServlet(urlPatterns = {"/about"})
public class AboutServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AboutServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("About Servlet: doGet");

        HttpSession session = req.getSession(false);
        if(session!=null)
        {
            String username = session.getAttribute("username").toString();
            Account account = Account.Find(username).orElse(null);
            if(account != null)
            {
                req.setAttribute("account", account);
            }
            else
            {
                logger.error("Account with useraname "+username+ " not found");
            }
        }
        else
        {
            logger.info("User not logged in. Session was null");

        }

        req.getRequestDispatcher("/_view/about.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("About Servlet: doPost");

    }

}
