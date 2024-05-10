package ru.arsentiev.servlet.guest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.controller.UserControllerDto;
import ru.arsentiev.dto.user.controller.UserLoginControllerDto;
import ru.arsentiev.dto.user.view.UserLoginViewDto;
import ru.arsentiev.manager.MapperManager;
import ru.arsentiev.manager.ServiceManager;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.servicelayer.service.UserService;
import ru.arsentiev.servicelayer.validator.entity.login.LoginError;
import ru.arsentiev.utils.JspPathCreator;
import ru.arsentiev.utils.UrlPathGetter;

import java.io.IOException;
import java.util.Optional;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_ERROR;
import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.JspPathGetter.LOGIN_JSP;
import static ru.arsentiev.utils.UrlPathGetter.LOGIN_URL;

@WebServlet(LOGIN_URL)
public class LoginServlet extends HttpServlet {
    private static UserService userService;
    private static UserMapper userMapper;

    @Override
    public void init() {
        userService = ServiceManager.getUserService();
        userMapper = MapperManager.getUserMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getDefaultPath(LOGIN_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final UserLoginViewDto userLoginViewDto = new UserLoginViewDto
                (req.getParameter("email"), req.getParameter("password"));
        final UserLoginControllerDto userLoginControllerDto = userMapper.map(userLoginViewDto);
        Optional<LoginError> error = userService.checkLoginUser(userLoginControllerDto);
        if (error.isEmpty()) {
            final UserControllerDto userControllerDto = userService.selectUser(userLoginControllerDto);
            req.getSession().setAttribute(NAME_ATTRIBUTE_USER, userControllerDto);
            switch (userControllerDto.role()) {
                case USER -> resp.sendRedirect(UrlPathGetter.USER_DEFAULT_URL);
                case ADMIN -> resp.sendRedirect(UrlPathGetter.ADMIN_DEFAULT_URL);
            }
        } else {
            req.setAttribute(NAME_ATTRIBUTE_ERROR, error.get());
            req.getRequestDispatcher(JspPathCreator.getDefaultPath(LOGIN_JSP)).forward(req, resp);
        }
    }
}
