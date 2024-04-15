package ru.arsentiev.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static ru.arsentiev.utils.UrlPathGetter.LOGOUT_URL;
import static ru.arsentiev.utils.UrlPathGetter.REGISTRATION_URL;

@WebServlet(LOGOUT_URL)
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().invalidate();
        resp.sendRedirect(REGISTRATION_URL);
    }
}
