package io.github.virtualstocksim.servlet;

import io.github.virtualstocksim.account.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

public class SessionValidater
{
    private static final Logger logger = LoggerFactory.getLogger(SessionValidater.class);

    /**
     * Checks if a valid user session is attached to the request
     * @param req Incoming Http request
     * @return Account if session was valid, Optional.empty() if not
     */
    public static Optional<Account> validate(HttpServletRequest req)
    {
        HttpSession session = req.getSession(false);
        if(session != null)
        {
            String uuid = (String) session.getAttribute("uuid");
            Optional<Account> account;
            if(uuid != null && !uuid.trim().isEmpty() && (account = Account.FindByUuid(uuid)).isPresent())
            {
                req.setAttribute("account", account.get());
                return account;
            }
            else
            {
                logger.error("Account with uuid " + uuid + " not found");
            }
        }
        else
        {
            logger.info("User not logged in. Session was null");
        }

        return Optional.empty();
    }
}
