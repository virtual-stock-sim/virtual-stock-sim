package io.github.virtualstocksim.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/compare"})
public class CompareServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(CompareServlet.class);

    @Override
    public void init() throws ServletException
    {
        DataStreamServlet.addListener("compareStream", new HttpRequestListener()
        {
            @Override
            public void onGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
            {
                logger.info("Listener doGet");
                resp.setContentType("text/plain");
                PrintWriter writer = resp.getWriter();
                writer.write("get received");
                writer.flush();
            }

            @Override
            public void onPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
            {
                resp.setContentType("text/plain");
                PrintWriter writer = resp.getWriter();
                writer.write("post received");
                writer.flush();
            }
        });
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Compare Servlet: doGet");

        req.getRequestDispatcher("/_view/compare.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Compare Servlet: doPost");
    }
}
