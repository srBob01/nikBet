package ru.arsentiev.servlet.user.profile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.controller.UserControllerDto;
import ru.arsentiev.dto.user.view.UserViewDto;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER_DTO;
import static ru.arsentiev.utils.JspPathGetter.USER_DEFAULT_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_DEFAULT_URL;

@WebServlet(USER_DEFAULT_URL)
public class UserDefaultServlet extends HttpServlet {
    private UserMapper userMapper;

    @Override
    public void init() throws ServletException {
        userMapper = UserMapper.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final UserControllerDto userControllerDto =
                (UserControllerDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        UserViewDto user = userMapper.map(userControllerDto);
        req.setAttribute(NAME_ATTRIBUTE_USER_DTO, user);
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_DEFAULT_JSP)).forward(req, resp);
    }

}
