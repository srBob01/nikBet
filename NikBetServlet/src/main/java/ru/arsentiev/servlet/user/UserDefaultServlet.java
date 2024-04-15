package ru.arsentiev.servlet.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;

import static ru.arsentiev.utils.JspPathGetter.USER_DEFAULT_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_DEFAULT_URL;

@WebServlet(USER_DEFAULT_URL)
public class UserDefaultServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_DEFAULT_JSP)).forward(req, resp);
    }
}
