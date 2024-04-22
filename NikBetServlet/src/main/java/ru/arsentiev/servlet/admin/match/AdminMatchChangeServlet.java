package ru.arsentiev.servlet.admin.match;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.game.controller.GameAdminCompletedControllerDto;
import ru.arsentiev.dto.game.controller.GameAdminProgressControllerDto;
import ru.arsentiev.dto.game.view.GameAdminCompletedViewDto;
import ru.arsentiev.dto.game.view.GameAdminProgressViewDto;
import ru.arsentiev.dto.game.view.GameInProgressViewDto;
import ru.arsentiev.mapper.GameMapper;
import ru.arsentiev.service.GameService;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.JspPathGetter.ADMIN_MATCH_CHANGE_JSP;
import static ru.arsentiev.utils.UrlPathGetter.ADMIN_MATCH_CHANGE_URL;

@WebServlet(ADMIN_MATCH_CHANGE_URL)
public class AdminMatchChangeServlet extends HttpServlet {
    private GameService gameService;
    private GameMapper gameMapper;

    @Override
    public void init() throws ServletException {
        gameService = GameService.getInstance();
        gameMapper = GameMapper.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final List<GameInProgressViewDto> listGameInProgressDto =
                gameService.selectGameInProgressAll()
                        .stream()
                        .map(gameMapper::mapProgressControllerToView)
                        .toList();
        req.setAttribute("listGameInProgressDto", listGameInProgressDto);
        req.getRequestDispatcher(JspPathCreator.getAdminPath(ADMIN_MATCH_CHANGE_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        switch (action) {
            case "updateMatch":
                updateMatch(req, resp);
                break;
            case "startSecondTime":
                startSecondHalf(req, resp);
                break;
            case "finishMatch":
                finishMatch(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    private void updateMatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idGame = req.getParameter("idGame");
        String goalHomeTeam = req.getParameter("goalHomeTeam");
        String goalGuestTeam = req.getParameter("goalGuestTeam");
        String coefficientOnHomeTeam = req.getParameter("coefficientOnHomeTeam");
        String coefficientOnDraw = req.getParameter("coefficientOnDraw");
        String coefficientOnGuestTeam = req.getParameter("coefficientOnGuestTeam");

        GameAdminProgressViewDto gameAdminProgressViewDto = GameAdminProgressViewDto.builder()
                .idGame(idGame)
                .goalHomeTeam(goalHomeTeam)
                .goalGuestTeam(goalGuestTeam)
                .coefficientOnHomeTeam(coefficientOnHomeTeam)
                .coefficientOnDraw(coefficientOnDraw)
                .coefficientOnGuestTeam(coefficientOnGuestTeam)
                .build();

        GameAdminProgressControllerDto gameAdminProgressControllerDto = gameMapper
                .mapAdminProgressControllerToViewGame(gameAdminProgressViewDto);

        if (gameService.updateDescriptionGame(gameAdminProgressControllerDto)) {
            req.setAttribute("messageGood", "You have changed the parameters of the game with the id " + idGame);
        } else {
            req.setAttribute("messageBad", "Mistake. The game with id" + idGame + " parameters have not" +
                                           " been changed");
        }

        doGet(req, resp);
    }

    private void startSecondHalf(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long idGame = Long.parseLong(req.getParameter("idGame"));
        if (gameService.startSecondHalf(idGame)) {
            req.setAttribute("messageGood", "You started the second half of game with id " + idGame);
        } else {
            req.setAttribute("messageBad", "Mistake. We didn't start the second half in game with id "
                                           + idGame);
        }
        doGet(req, resp);
    }

    private void finishMatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idGame = req.getParameter("idGame");
        String[] parts = req.getParameter("score").split(" - ");
        String goalHomeTeam = parts[0];
        String goalGuestTeam = parts[1];
        GameAdminCompletedViewDto gameAdminCompletedViewDto = GameAdminCompletedViewDto.builder()
                .idGame(idGame)
                .goalGuestTeam(goalGuestTeam)
                .goalHomeTeam(goalHomeTeam)
                .build();
        GameAdminCompletedControllerDto gameAdminCompletedControllerDto = gameMapper
                .mapAdminCompletedViewToControllerGame(gameAdminCompletedViewDto);

        if (gameService.endGame(gameAdminCompletedControllerDto)) {
            req.setAttribute("messageGood", "The game with id " + idGame + " is over. All bets are paid ");
        } else {
            req.setAttribute("messageBad", "Mistake.The game with id"
                                           + idGame + " is not finished. Try again ");
        }
        doGet(req, resp);
    }
}
