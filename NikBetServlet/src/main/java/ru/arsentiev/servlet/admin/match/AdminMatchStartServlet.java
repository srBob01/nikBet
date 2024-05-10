package ru.arsentiev.servlet.admin.match;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.game.view.GameScheduledViewDto;
import ru.arsentiev.manager.MapperManager;
import ru.arsentiev.manager.ServiceManager;
import ru.arsentiev.mapper.GameMapper;
import ru.arsentiev.servicelayer.service.GameService;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.JspPathGetter.ADMIN_MATCH_START_JSP;
import static ru.arsentiev.utils.UrlPathGetter.ADMIN_MATCH_START_URL;

@WebServlet(ADMIN_MATCH_START_URL)
public class AdminMatchStartServlet extends HttpServlet {
    private GameService gameService;
    private GameMapper gameMapper;

    @Override
    public void init() throws ServletException {
        gameMapper = MapperManager.getGameMapper();
        gameService = ServiceManager.getGameService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<GameScheduledViewDto> listGameScheduledDto = gameService.selectHotGame()
                .stream()
                .map(gameMapper::mapScheduledControllerToView)
                .toList();
        req.setAttribute("listGameScheduledDto", listGameScheduledDto);
        req.getRequestDispatcher(JspPathCreator.getAdminPath(ADMIN_MATCH_START_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long idGame = Long.parseLong(req.getParameter("idGame"));
        if (gameService.startGame(idGame)) {
            req.setAttribute("messageGood", "You started the game with an id " + idGame);
        } else {
            req.setAttribute("messageBad", "The game with the id " + idGame + " has not been started");
        }
        doGet(req, resp);
    }
}
