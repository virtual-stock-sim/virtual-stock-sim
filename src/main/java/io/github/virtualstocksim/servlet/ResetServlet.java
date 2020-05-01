package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.PasswordResetManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/reset"})



public class ResetServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        System.out.println("Reset servlet: doGet");
        // call JSP to generate empty form
        req.getRequestDispatcher("/_view/reset.jsp").forward(req, resp);
    }

    @Override
    protected void  doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        System.out.println("Reset servlet: doPost");
        PasswordResetManager prm = new PasswordResetManager();
        req.setAttribute("manager",prm);
        prm.setEmail(req.getParameter("userInput"));
        prm.sendMailWithLink();
        req.getRequestDispatcher("/_view/reset.jsp").forward(req, resp);
    }



}
