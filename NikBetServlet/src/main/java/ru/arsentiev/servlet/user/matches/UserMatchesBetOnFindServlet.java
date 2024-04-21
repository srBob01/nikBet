package ru.arsentiev.servlet.user.matches;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;

import static ru.arsentiev.utils.JspPathGetter.USER_MATCHES_FIND_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_MATCHES_FIND_BET_URL;

@WebServlet(USER_MATCHES_FIND_BET_URL)
public class UserMatchesBetOnFindServlet extends UserMatchesBaseServlet {
    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("teams", getServletContext().getAttribute("teams"));
        req.setAttribute("gameStatuses", getServletContext().getAttribute("gameStatuses"));
        req.setAttribute("gameResults", getServletContext().getAttribute("gameResults"));
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_MATCHES_FIND_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
