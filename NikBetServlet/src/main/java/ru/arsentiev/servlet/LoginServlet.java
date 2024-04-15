package ru.arsentiev.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.UserViewLoginDto;
import ru.arsentiev.service.UserService;
import ru.arsentiev.service.entity.user.ReturnValueInCheckLogin;
import ru.arsentiev.utils.JspPathCreator;
import ru.arsentiev.utils.UrlPathGetter;

import java.io.IOException;

import static ru.arsentiev.utils.AttributeGetter.*;
import static ru.arsentiev.utils.JspPathGetter.LOGIN_JSP;
import static ru.arsentiev.utils.UrlPathGetter.LOGIN_URL;

@WebServlet(LOGIN_URL)
public class LoginServlet extends HttpServlet {
    private static UserService userService;

    @Override
    public void init() {
        userService = UserService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getDefaultPath(LOGIN_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserViewLoginDto userViewLoginDto = new UserViewLoginDto
                (req.getParameter("email"), req.getParameter("password"));
        ReturnValueInCheckLogin value = userService.checkLogin(userViewLoginDto);
        if (value.userDto().isPresent()) {
            req.getSession().setAttribute(NAME_ATTRIBUTE_USER, value.userDto().get());
            resp.sendRedirect(UrlPathGetter.USER_DEFAULT_URL);
        } else {
            req.setAttribute(NAME_ATTRIBUTE_ERROR, value.loginError());
            req.getRequestDispatcher(JspPathCreator.getDefaultPath(LOGIN_JSP)).forward(req, resp);
        }
    }
}
