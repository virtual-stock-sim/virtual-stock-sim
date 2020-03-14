package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.stock.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

public class TestDisplayStockServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(TestDisplayStockServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("TestDisplayStock Servlet: doGet");

        Stock stock = new Stock(0, "TEST_SYM", new BigDecimal("500.30"), 0);
        req.setAttribute("stock", stock);

        req.getRequestDispatcher("/_view/testDisplayStocks.jsp").forward(req, resp);
    }
}
