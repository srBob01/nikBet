package ru.arsentiev.servlet.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.UserDto;
import ru.arsentiev.dto.user.UserMoneyViewDto;
import ru.arsentiev.service.UserService;
import ru.arsentiev.utils.JspPathCreator;
import ru.arsentiev.validator.entity.money.MoneyError;

import java.io.IOException;
import java.util.Optional;

import static ru.arsentiev.utils.AttributeGetter.*;
import static ru.arsentiev.utils.JspPathGetter.USER_MONEY_DEPOSIT_JSP;
import static ru.arsentiev.utils.UrlPathGetter.*;

@WebServlet(USER_MONEY_DEPOSIT_URL)
public class UserMoneyDepositServlet extends HttpServlet {
    private static UserService userService;

    @Override
    public void init() throws ServletException {
        userService = UserService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_MONEY_DEPOSIT_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDto user = (UserDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        String summa = req.getParameter("amount");
        UserMoneyViewDto userMoneyViewDto = new UserMoneyViewDto(user.idUser(), summa);
        Optional<MoneyError> error = userService.depositMoney(userMoneyViewDto);
        if (error.isEmpty()) {
            resp.sendRedirect(USER_MONEY_DEFAULT_URL);
        } else {
            req.setAttribute(NAME_ATTRIBUTE_ERROR, error.get());
            doGet(req, resp);
        }
    }
}
