package ru.arsentiev.servlet.admin.delete;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.manager.MapperManager;
import ru.arsentiev.manager.ServiceManager;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.servicelayer.service.UserService;

import java.io.IOException;

public class AdminBaseDeleteServlet extends HttpServlet {
    protected UserService userService;

    protected UserMapper userMapper;

    @Override
    public void init() throws ServletException {
        userService = ServiceManager.getUserService();
        userMapper = MapperManager.getUserMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idUser = req.getParameter("userId");
        if (userService.deleteUser(Long.parseLong(idUser))) {
            req.setAttribute("message", "The user with the id " + idUser + " has been successfully deleted");
        } else {
            req.setAttribute("message", "error. The user with the id " + idUser + " has not been deleted");
        }
        doGet(req, resp);
    }
}
