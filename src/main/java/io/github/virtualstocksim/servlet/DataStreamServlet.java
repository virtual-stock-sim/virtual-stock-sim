package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.stock.stockrequest.StockRequestHandler;
import org.eclipse.jetty.io.RuntimeIOException;
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
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet(urlPatterns = {"/dataStream"}, asyncSupported = true)
public class DataStreamServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(DataStreamServlet.class);

    private static final ConcurrentHashMap<String, AsyncContext> clients = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, AsyncContext> getConnectedClients() { return clients; }

    // Custom listeners to run for relevant requests.
    private static final ConcurrentHashMap<String, HttpRequestListener> listeners = new ConcurrentHashMap<>();

    private static final String STREAM_HEADER = "text/event-stream";

    @Override
    public void init() throws ServletException
    {
        addListener("stockRequest", new StockRequestHandler());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("DataStreamServlet: doGet");

        // Is the request a new stream connection
        if(req.getHeader("Accept").equals(STREAM_HEADER))
        {
            String id = UUID.randomUUID().toString();
            logger.info("New Connection - Id: " + id);

            // Setup stream headers
            resp.setContentType(STREAM_HEADER);
            resp.setHeader("Cache-Control", "no-cache");
            resp.setHeader("Connection", "Keep-Alive");
            resp.setCharacterEncoding("UTF-8");

            // Start the async context
            final AsyncContext ac = req.startAsync(req, resp);
            ac.addListener(new AcListener(id));
            // 2 Minute timeout for connection
            int timeout = 1000*60*2;
            ac.setTimeout(timeout);

            // Send the context id to the client
            sendSimpleMessage(ac, "id=" + id);

            clients.put(id, ac);
        }
        else
        {
            String name = req.getHeader("Listener-name");
            if(name != null)
            {
                listeners.get(name).onGet(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("DataStreamServlet: doPost");

        // Is this a notification that the client is disconnecting
        String op = req.getParameter("op");
        String id;
        if(op != null && op.equals("close") && (id = req.getHeader("id")) != null)
        {
            AsyncContext ac = clients.remove(id);
            if(ac != null)
            {
                ac.complete();
                logger.info("Connection close request completed successfully - ID: " + id);
            }
        }
        else
        {
            String name = req.getHeader("Listener-name");
            if(name != null)
            {
                listeners.get(name).onPost(req, resp);
            }
        }
    }

    /**
     * Sends a message to a context and handles any exceptions
     * The method adds the event type and `data:` label
     * @param contextId Id of client AsyncContext to write to
     * @param msg Message
     */
    public static void sendSimpleMessage(String contextId, String msg)
    {
        sendSimpleMessage(clients.get(contextId), msg);
    }

    /**
     * Sends the given message as is with no modification and handles any exceptions
     * @param contextId Id of client AsyncContext to write to
     * @param msg Message
     */
    public static void sendMessage(String contextId, String msg)
    {
        sendMessage(clients.get(contextId), msg);
    }

    /**
     * Sends a message to a context and handles any exceptions
     * The method adds the event type and `data:` label
     * @param context Context to write to
     * @param msg Message
     */
    public static void sendSimpleMessage(AsyncContext context, String msg)
    {
        sendMessage(context, "event: message\ndata: " + msg + "\n\n");
    }

    /**
     * Sends the given message as is with no modification and handles any exceptions
     * @param context AsyncContext to write to
     * @param msg Message
     */
    public static void sendMessage(AsyncContext context, String msg)
    {
        try
        {
            PrintWriter w = context.getResponse().getWriter();
            w.write(msg);
            w.flush();
        }
        catch (IOException | RuntimeIOException e)
        {
            logger.warn("Exception writing to AsyncContext client. Closing context\n", e);
            context.complete();
        }
    }

    /**
     * Registers a listener
     * Listeners block the current thread that the request is being received on
     * @param name Name of the listener. Must correspond to the value of "streamName" header in request
     * @param listener Listener to register
     */
    public static void addListener(String name, HttpRequestListener listener) { listeners.put(name, listener); }
    public static void removeListener(String name) { listeners.remove(name); }

    private static class AcListener implements AsyncListener
    {
        private final String id;
        public String getId() { return this.id; }
        public AcListener(String id)
        {
            this.id = id;
        }

        private String removeReason = "Complete";

        @Override
        public void onComplete(AsyncEvent event) throws IOException
        {
            logger.info("AsyncContext closed. Reason: " + removeReason + " - Id: " + id);
            clients.remove(this.id);
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException
        {
            removeReason = "Timeout";
        }

        @Override
        public void onError(AsyncEvent event) throws IOException
        {
            removeReason = "Error";
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException { }
    }
}
