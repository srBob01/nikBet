package ru.arsentiev.servlet.user.money;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.controller.UserControllerDto;
import ru.arsentiev.manager.ServiceManager;
import ru.arsentiev.service.UserService;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.math.BigDecimal;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.JspPathGetter.USER_MONEY_DEFAULT_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_MONEY_DEFAULT_URL;

@WebServlet(USER_MONEY_DEFAULT_URL)
public class UserMoneyDefaultServlet extends HttpServlet {
    private static UserService userService;

    @Override
    public void init() throws ServletException {
        userService = ServiceManager.getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final UserControllerDto user = (UserControllerDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        final BigDecimal balance = userService.getAccountBalance(user.idUser());
        req.setAttribute("balance", balance.toString());
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_MONEY_DEFAULT_JSP)).forward(req, resp);
    }
}
