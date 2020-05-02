package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.PasswordResetManager;
import io.github.virtualstocksim.account.ResetToken;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;

@WebServlet(urlPatterns = {"/reset"})



public class ResetServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    //private List<String> resetSalts = ResetToken.FindByAccountId() ;
    private String resetSalt;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        System.out.println("Reset servlet: doGet");
        String salt= req.getParameter("token");

        if(salt!=null && !salt.trim().isEmpty()) {
             String token = req.getParameter("token");
             System.out.println("trying to find token: "+token );
                if(ResetToken.Find(token).orElseGet(null)!=null) {
                    req.setAttribute("salt", token);
                }

             //we want to check here that the token is brand new and unique
            System.out.println("token from this: "+resetSalt);
            System.out.println("token from JSP: " + salt);
        }

        req.getRequestDispatcher("/_view/reset.jsp").forward(req, resp);
    }

    @Override
    protected void  doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        System.out.println("Reset servlet: doPost");
        PasswordResetManager prm = new PasswordResetManager();
        prm.setEmail(req.getParameter("userInput"));

        prm.sendMailWithLink();
        Account localAcct=Account.Find(prm.getUsername()).get();
        System.out.println("giving token: " + prm.getResetSalt() + " to the resetToken class");
        String x = prm.getResetSalt();
        ResetToken.Create(localAcct.getId(),x, Timestamp.valueOf("2021-03-12 20:45:00"));
        req.getRequestDispatcher("/_view/reset.jsp").forward(req, resp);
    }



}
