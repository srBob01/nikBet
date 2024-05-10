package ru.arsentiev.servlet.user.matches;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.game.view.GameCompletedViewDto;
import ru.arsentiev.manager.MapperManager;
import ru.arsentiev.manager.ServiceManager;
import ru.arsentiev.mapper.GameMapper;
import ru.arsentiev.servicelayer.service.GameService;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.JspPathGetter.USER_MATCHES_ALL_COMPLETED_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_MATCHES_ALL_COMPLETED_URL;

@WebServlet(USER_MATCHES_ALL_COMPLETED_URL)
public class UserMatchesAllCompletedServlet extends HttpServlet {
    private GameService gameService;
    private GameMapper gameMapper;

    @Override
    public void init() throws ServletException {
        gameService = ServiceManager.getGameService();
        gameMapper = MapperManager.getGameMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final List<GameCompletedViewDto> listGameCompletedDto =
                gameService.selectGameCompletedAll()
                        .stream().map(gameMapper::mapCompletedControllerToView)
                        .toList();
        req.setAttribute("listGameCompletedDto", listGameCompletedDto);
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_MATCHES_ALL_COMPLETED_JSP)).forward(req, resp);
    }
}