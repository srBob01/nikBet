package ru.arsentiev.servlet.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.UserDto;
import ru.arsentiev.service.UserService;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.JspPathGetter.USER_MONEY_DEFAULT_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_MONEY_DEFAULT_URL;

@WebServlet(USER_MONEY_DEFAULT_URL)
public class UserMoneyDefaultServlet extends HttpServlet {
    private static UserService userService;

    @Override
    public void init() throws ServletException {
        userService = UserService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDto user = (UserDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        String balance = userService.getAccountBalance(user.idUser());
        req.setAttribute("balance", balance);
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_MONEY_DEFAULT_JSP)).forward(req, resp);
    }
}
