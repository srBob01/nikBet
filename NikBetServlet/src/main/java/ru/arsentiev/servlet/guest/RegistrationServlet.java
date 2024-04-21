package ru.arsentiev.servlet.guest;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.controller.UserRegistrationControllerDto;
import ru.arsentiev.dto.user.view.UserRegistrationViewDto;
import ru.arsentiev.manager.ValidationManager;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.service.UserService;
import ru.arsentiev.utils.JspPathCreator;
import ru.arsentiev.validator.RegistrationUserValidator;
import ru.arsentiev.validator.entity.load.LoadValidationResult;

import java.io.IOException;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_ERRORS;
import static ru.arsentiev.utils.JspPathGetter.REGISTRATION_JSP;
import static ru.arsentiev.utils.UrlPathGetter.LOGIN_URL;
import static ru.arsentiev.utils.UrlPathGetter.REGISTRATION_URL;

@WebServlet(REGISTRATION_URL)
public class RegistrationServlet extends HttpServlet {

    private static UserService userService;
    private RegistrationUserValidator registrationUserValidator;
    private static UserMapper userMapper;


    @Override
    public void init(ServletConfig config) {
        userService = UserService.getInstance();
        registrationUserValidator = ValidationManager.getRegistrationUserValidator();
        userMapper = UserMapper.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getDefaultPath(REGISTRATION_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        final UserRegistrationViewDto userRegistrationViewDto = getUserRegistrationViewDto(req);
        final LoadValidationResult result = registrationUserValidator.isValid(userRegistrationViewDto);

        if (result.isEmpty()) {
            UserRegistrationControllerDto userRegistrationControllerDto = userMapper.map(userRegistrationViewDto);
            userService.insertUser(userRegistrationControllerDto);
            resp.sendRedirect(LOGIN_URL);
        } else {
            req.setAttribute(NAME_ATTRIBUTE_ERRORS, result.getLoadErrors());
            doGet(req, resp);
        }
    }

    private static UserRegistrationViewDto getUserRegistrationViewDto(HttpServletRequest req) {
        return UserRegistrationViewDto.builder()
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
