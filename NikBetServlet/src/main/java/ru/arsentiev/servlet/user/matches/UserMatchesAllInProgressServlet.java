package ru.arsentiev.servlet.user.matches;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.game.view.GameInProgressViewDto;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.JspPathGetter.USER_MATCHES_ALL_IN_PROGRESS_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_MATCHES_ALL_IN_PROGRESS_URL;

@WebServlet(USER_MATCHES_ALL_IN_PROGRESS_URL)
public class UserMatchesAllInProgressServlet extends UserMatchesBaseServlet {
    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final List<GameInProgressViewDto> listGameInProgressDto =
                gameService.selectGameInProgressAll()
                        .stream()
                        .map(gameMapper::mapProgressControllerToView)
                        .toList();
        req.setAttribute("listGameInProgressDto", listGameInProgressDto);
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_MATCHES_ALL_IN_PROGRESS_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
