package ru.arsentiev.servlet.user.matches;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.game.view.GameCompletedViewDto;
import ru.arsentiev.dto.game.view.GameInProgressViewDto;
import ru.arsentiev.dto.game.view.GameScheduledViewDto;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.JspPathGetter.USER_MATCHES_DEFAULT_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_MATCHES_DEFAULT_URL;

@WebServlet(USER_MATCHES_DEFAULT_URL)
public class UserMatchesDefaultServlet extends UserMatchesBaseServlet {
    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<GameInProgressViewDto> listGameInProgressDto =
                gameService.selectGameInProgressLimit()
                        .stream()
                        .map(gameMapper::mapProgressControllerToView)
                        .toList();
        List<GameScheduledViewDto> listGameScheduledDto =
                gameService.selectGameScheduledLimit()
                        .stream()
                        .map(gameMapper::mapScheduledControllerToView)
                        .toList();
        List<GameCompletedViewDto> listGameCompletedDto =
                gameService.selectGameCompletedLimit()
                        .stream()
                        .map(gameMapper::mapCompletedControllerToView)
                        .toList();
        req.setAttribute("listGameInProgressDto", listGameInProgressDto);
        req.setAttribute("listGameScheduledDto", listGameScheduledDto);
        req.setAttribute("listGameCompletedDto", listGameCompletedDto);
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_MATCHES_DEFAULT_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
