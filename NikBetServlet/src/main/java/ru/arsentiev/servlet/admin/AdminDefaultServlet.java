package ru.arsentiev.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;

import static ru.arsentiev.utils.JspPathGetter.ADMIN_DEFAULT_JSP;
import static ru.arsentiev.utils.UrlPathGetter.ADMIN_DEFAULT_URL;

@WebServlet(ADMIN_DEFAULT_URL)
public class AdminDefaultServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getAdminPath(ADMIN_DEFAULT_JSP)).forward(req, resp);
    }
}
