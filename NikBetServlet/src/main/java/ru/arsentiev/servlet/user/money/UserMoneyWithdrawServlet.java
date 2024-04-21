package ru.arsentiev.servlet.user.money;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.controller.UserControllerDto;
import ru.arsentiev.dto.user.controller.UserMoneyControllerDto;
import ru.arsentiev.dto.user.view.UserMoneyViewDto;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.service.UserService;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_ERROR;
import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.JspPathGetter.USER_MONEY_WITHDRAW_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_MONEY_DEFAULT_URL;
import static ru.arsentiev.utils.UrlPathGetter.USER_MONEY_WITHDRAW_URL;

@WebServlet(USER_MONEY_WITHDRAW_URL)
public class UserMoneyWithdrawServlet extends HttpServlet {
    private static UserService userService;
    private static UserMapper userMapper;

    @Override
    public void init() {
        userService = UserService.getInstance();
        userMapper = UserMapper.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_MONEY_WITHDRAW_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final UserControllerDto user = (UserControllerDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        final String summa = req.getParameter("amount");
        final UserMoneyViewDto userMoneyViewDto = UserMoneyViewDto.builder()
                .idUser(user.idUser())
                .summa(summa)
                .build();
        final UserMoneyControllerDto userMoneyControllerDto = userMapper.map(userMoneyViewDto);
        if (userService.withdrawMoney(userMoneyControllerDto)) {
            resp.sendRedirect(USER_MONEY_DEFAULT_URL);
        } else {
            req.setAttribute(NAME_ATTRIBUTE_ERROR, "Not enough money");
            doGet(req, resp);
        }
    }
}
