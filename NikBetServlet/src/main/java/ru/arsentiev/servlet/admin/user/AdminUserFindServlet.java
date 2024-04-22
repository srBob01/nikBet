package ru.arsentiev.servlet.admin.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.controller.UserForAdminControllerDto;
import ru.arsentiev.dto.user.view.UserForAdminViewDto;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.service.UserService;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.Optional;

import static ru.arsentiev.utils.JspPathGetter.ADMIN_USER_FIND_JSP;
import static ru.arsentiev.utils.UrlPathGetter.ADMIN_USER_FIND_URL;

@WebServlet(ADMIN_USER_FIND_URL)
public class AdminUserFindServlet extends HttpServlet {
    private UserService userService;
    private UserMapper userMapper;

    @Override
    public void init() throws ServletException {
        userService = UserService.getInstance();
        userMapper = UserMapper.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspPathCreator.getAdminPath(ADMIN_USER_FIND_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nickname = req.getParameter("nickname");
        Optional<UserForAdminControllerDto> userForAdminControllerDto = userService.selectUserByNickname(nickname);
        if (userForAdminControllerDto.isEmpty()) {
            req.setAttribute("messageNotFound", "The user with the nickname " + nickname + " is missing");
        } else {
            UserForAdminViewDto userForAdminViewDto = userMapper
                    .mapUserControllerToViewForAdmin(userForAdminControllerDto.get());
            req.setAttribute("userDto", userForAdminViewDto);
        }
        doGet(req, resp);
    }
}
