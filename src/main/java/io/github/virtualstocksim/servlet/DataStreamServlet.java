package io.github.virtualstocksim.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet(urlPatterns = {"/dataStream"}, asyncSupported = true)
public class DataStreamServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(DataStreamServlet.class);
    public static final ConcurrentHashMap<String, AsyncContext> clients = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("DataStreamServlet: doGet");

        resp.setContentType("text/event-stream");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setCharacterEncoding("UTF-8");

        final AsyncContext ac = req.startAsync(req, resp);
        ac.addListener(new AcListener());
        ac.setTimeout(0);
        clients.put(req.getSession(true).getId(), ac);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

    }

    private static class AcListener implements AsyncListener
    {
        private static void remove(AsyncEvent e)
        {
            HttpServletRequest req = (HttpServletRequest) e.getAsyncContext().getRequest();
            clients.remove(req.getSession().getId());
        }

        @Override
        public void onComplete(AsyncEvent event) throws IOException
        {
            remove(event);
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException
        {
            remove(event);
        }

        @Override
        public void onError(AsyncEvent event) throws IOException
        {
            remove(event);
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException { }
    }
}
