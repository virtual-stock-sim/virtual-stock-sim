package io.github.virtualstocksim.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CreateAccountServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(io.github.virtualstocksim.servlet.CreateAccountServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Create Account Servlet: doGet");

        req.getRequestDispatcher("/_view/createAccount.jsp").forward(req, resp);
    }

}
