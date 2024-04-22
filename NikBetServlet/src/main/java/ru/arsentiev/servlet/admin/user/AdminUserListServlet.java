package ru.arsentiev.servlet.admin.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.user.view.UserForAdminViewDto;
import ru.arsentiev.servlet.admin.delete.AdminBaseDeleteServlet;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.JspPathGetter.ADMIN_USER_LIST_JSP;
import static ru.arsentiev.utils.UrlPathGetter.ADMIN_USER_LIST_URL;

@WebServlet(ADMIN_USER_LIST_URL)
public class AdminUserListServlet extends AdminBaseDeleteServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<UserForAdminViewDto> userForAdminViewDtoList = userService.selectAllUser()
                .stream()
                .map(userMapper::mapUserControllerToViewForAdmin)
                .toList();
        req.setAttribute("userForAdminViewDtoList", userForAdminViewDtoList);
        req.getRequestDispatcher(JspPathCreator.getAdminPath(ADMIN_USER_LIST_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
