package ru.arsentiev.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.jsp.user.UserRegSerConDto;
import ru.arsentiev.service.UserService;
import ru.arsentiev.service.entity.ReturnValueInInsertUser;
import ru.arsentiev.utils.JspHelper;

import java.io.IOException;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    private static UserService userService;

    @Override
    public void init(ServletConfig config) {
        userService = UserService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspHelper.getPath("registration")).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        UserRegSerConDto userDto = UserRegSerConDto.builder()
                .nickname(req.getParameter("nickname"))
                .firstName(req.getParameter("firstName"))
                .lastName(req.getParameter("lastName"))
                .patronymic(req.getParameter("patronymic"))
                .email(req.getParameter("email"))
                .phoneNumber(req.getParameter("phoneNumber"))
                .password(req.getParameter("password"))
                .birthDate(req.getParameter("birthDate"))
                .build();

        ReturnValueInInsertUser result = userService.insertUser(userDto);
        if (result.myErrors().isEmpty()) {
            resp.sendRedirect("/login");
        } else {
            req.setAttribute("errors", result.myErrors());
            doGet(req, resp);
        }
    }
}
