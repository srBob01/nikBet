package ru.arsentiev.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.UserRegistrationDto;
import ru.arsentiev.service.UserService;
import ru.arsentiev.utils.AttributeGetter;
import ru.arsentiev.utils.JspPathCreator;
import ru.arsentiev.validator.entity.load.LoadError;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.AttributeGetter.*;
import static ru.arsentiev.utils.JspPathGetter.REGISTRATION_JSP;
import static ru.arsentiev.utils.UrlPathGetter.LOGIN_URL;
import static ru.arsentiev.utils.UrlPathGetter.REGISTRATION_URL;

@WebServlet(REGISTRATION_URL)
public class RegistrationServlet extends HttpServlet {

    private static UserService userService;

    @Override
    public void init(ServletConfig config) {
        userService = UserService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getDefaultPath(REGISTRATION_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        final var userDto = getUserRegistrationDto(req);

        List<LoadError> result = userService.insertUser(userDto);
        if (result.isEmpty()) {
            resp.sendRedirect(LOGIN_URL);
        } else {
            req.setAttribute(NAME_ATTRIBUTE_ERRORS, result);
            doGet(req, resp);
        }
    }

    private static UserRegistrationDto getUserRegistrationDto(HttpServletRequest req) {
        return UserRegistrationDto.builder()
                .nickname(req.getParameter("nickname"))
                .firstName(req.getParameter("firstName"))
                .lastName(req.getParameter("lastName"))
                .patronymic(req.getParameter("patronymic"))
                .email(req.getParameter("email"))
                .phoneNumber(req.getParameter("phoneNumber"))
                .password(req.getParameter("password"))
                .birthDate(req.getParameter("birthDate"))
                .build();
    }
}
