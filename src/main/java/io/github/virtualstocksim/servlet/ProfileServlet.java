package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.AccountController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.zip.DataFormatException;

public class ProfileServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Profile Servlet: doGet");

        req.getRequestDispatcher("/_view/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AccountController controller = new AccountController();

        logger.info("Profile Servlet: doPost");

            if(req.getParameter("bio") !=null) {
                // User is editing bio
                String newBio = req.getParameter("newBio");


            }else if (req.getParameter("profilePic")!=null) {
                // user is editing profile picture

            }else if (req.getParameter("newLogin")!=null) {
                // user is changing login credentials

            }





    }

}
