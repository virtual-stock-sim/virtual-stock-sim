package io.github.virtualstocksim.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LandingServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LandingServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Landing Servlet: doGet");

        // logout user if they were logged in
        HttpSession session = req.getSession(false);
        if(session!=null){
            session.invalidate();
        }

        req.getRequestDispatcher("/_view/landing.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("Landing Servlet: doPost");

    }

}
