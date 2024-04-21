package ru.arsentiev.servlet.user.update;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.controller.UserControllerDto;
import ru.arsentiev.dto.user.controller.UserLogoPasControllerDto;
import ru.arsentiev.dto.user.view.UserLogoPasViewDto;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.service.UserService;
import ru.arsentiev.utils.JspPathCreator;
import ru.arsentiev.validator.entity.update.UpdatePasswordError;

import java.io.IOException;
import java.util.Optional;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_ERROR;
import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.JspPathGetter.USER_UPDATE_PASSWORD_JSP;
import static ru.arsentiev.utils.UrlPathGetter.*;


@WebServlet(USER_UPDATE_PASSWORD_URL)
public class UserUpdatePasswordServlet extends HttpServlet {
    private static UserService userService;
    private UserMapper userMapper;

    @Override
    public void init() throws ServletException {
        userService = UserService.getInstance();
        userMapper = UserMapper.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_UPDATE_PASSWORD_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserControllerDto user = (UserControllerDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        String email = user.email();
        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");
        UserLogoPasViewDto userLogoPasViewDto = UserLogoPasViewDto.builder()
                .email(email)
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();
        UserLogoPasControllerDto userLogoPasControllerDto = userMapper.map(userLogoPasViewDto);
        Optional<UpdatePasswordError> error = userService.updatePassword(userLogoPasControllerDto);
        if (error.isEmpty()) {
            resp.sendRedirect(USER_DEFAULT_URL);
        } else {
            req.setAttribute(NAME_ATTRIBUTE_ERROR, error.get());
            doGet(req, resp);
        }
    }
}
