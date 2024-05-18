package ru.arsentiev.servlet.guest;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.view.UserRegistrationViewDto;
import ru.arsentiev.manager.ServiceManager;
import ru.arsentiev.servicelayer.service.UserService;
import ru.arsentiev.servicelayer.validator.entity.load.LoadError;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_ERRORS;
import static ru.arsentiev.utils.JspPathGetter.REGISTRATION_JSP;
import static ru.arsentiev.utils.UrlPathGetter.LOGIN_URL;
import static ru.arsentiev.utils.UrlPathGetter.REGISTRATION_URL;

@WebServlet(REGISTRATION_URL)
public class RegistrationServlet extends HttpServlet {

    private static UserService userService;

    @Override
    public void init(ServletConfig config) {
        userService = ServiceManager.getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getDefaultPath(REGISTRATION_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        final UserRegistrationViewDto userRegistrationViewDto = getUserRegistrationViewDto(req);
        final List<LoadError> result = userService.insertUser(userRegistrationViewDto);

        if (result.isEmpty()) {
            resp.sendRedirect(LOGIN_URL);
        } else {
            req.setAttribute(NAME_ATTRIBUTE_ERRORS, result);
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
