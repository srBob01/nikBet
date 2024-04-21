package ru.arsentiev.servlet.user.matches;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.game.view.GameScheduledViewDto;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.JspPathGetter.USER_MATCHES_ALL_SCHEDULED_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_MATCHES_ALL_SCHEDULED_URL;

@WebServlet(USER_MATCHES_ALL_SCHEDULED_URL)
public class UserMatchesAllScheduledServlet extends UserMatchesBaseServlet {
    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final List<GameScheduledViewDto> listGameScheduledDto =
                gameService.selectGameScheduledAll()
                        .stream().map(gameMapper::mapScheduledControllerToView)
                        .toList();
        req.setAttribute("listGameScheduledDto", listGameScheduledDto);
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_MATCHES_ALL_SCHEDULED_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}