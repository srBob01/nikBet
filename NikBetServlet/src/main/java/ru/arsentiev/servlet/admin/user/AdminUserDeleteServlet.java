package ru.arsentiev.servlet.admin.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.servlet.admin.delete.AdminBaseDeleteServlet;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;

import static ru.arsentiev.utils.JspPathGetter.ADMIN_USER_FIND_JSP;
import static ru.arsentiev.utils.UrlPathGetter.ADMIN_USER_DELETE_URL;

@WebServlet(ADMIN_USER_DELETE_URL)
public class AdminUserDeleteServlet extends AdminBaseDeleteServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getAdminPath(ADMIN_USER_FIND_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
