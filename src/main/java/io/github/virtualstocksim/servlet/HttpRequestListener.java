package io.github.virtualstocksim.servlet;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EventListener;

public interface HttpRequestListener extends EventListener
{
    public void onGet(HttpServletRequest req, HttpServletResponse resp) throws IOException;
    public void onPost(HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
