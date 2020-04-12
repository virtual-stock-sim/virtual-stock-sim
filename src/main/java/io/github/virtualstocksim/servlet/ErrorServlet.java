package io.github.virtualstocksim.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ErrorServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ErrorServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Error Servlet: doGet");

        // do not create a new session until the user logs in
        HttpSession session = req.getSession(false);

        req.getRequestDispatcher("/_view/404.jsp").forward(req, resp);
    }
}
