package ru.arsentiev.servlet.user.matches;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.game.controller.GameParametersControllerDto;
import ru.arsentiev.dto.game.view.GameParametersViewDto;
import ru.arsentiev.dto.game.view.GameCompletedViewDto;
import ru.arsentiev.dto.game.view.GameInProgressViewDto;
import ru.arsentiev.dto.game.view.GameScheduledViewDto;
import ru.arsentiev.dto.team.view.TeamViewDto;
import ru.arsentiev.entity.GameResult;
import ru.arsentiev.entity.GameStatus;
import ru.arsentiev.manager.MapperManager;
import ru.arsentiev.manager.ServiceManager;
import ru.arsentiev.mapper.GameMapper;
import ru.arsentiev.mapper.TeamMapper;
import ru.arsentiev.servicelayer.service.GameService;
import ru.arsentiev.servicelayer.service.TeamService;
import ru.arsentiev.servicelayer.service.entity.game.TripleListOfGameControllerDto;
import ru.arsentiev.processing.query.entity.CompletedGameFields;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static ru.arsentiev.utils.JspPathGetter.USER_MATCHES_FIND_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_MATCHES_FIND_URL;

@WebServlet(USER_MATCHES_FIND_URL)
public class UserMatchesFindServlet extends HttpServlet {
    private GameService gameService;
    protected GameMapper gameMapper;

    @Override
    public void init() throws ServletException {
        gameMapper = MapperManager.getGameMapper();
        gameService = ServiceManager.getGameService();
        final TeamMapper teamMapper = MapperManager.getTeamMapper();
        final TeamService teamService = ServiceManager.getTeamService();
        final List<TeamViewDto> teamViewDtoList = teamService.selectAllTeam()
                .stream()
                .map(teamMapper::map)
                .toList();
        final List<GameStatus> gameStatusList = Arrays.stream(GameStatus.values()).toList();
        final List<GameResult> gameResultList = Arrays.stream(GameResult.values()).toList();
        getServletContext().setAttribute("teams", teamViewDtoList);
        getServletContext().setAttribute("gameStatuses", gameStatusList);
        getServletContext().setAttribute("gameResults", gameResultList);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("teams", getServletContext().getAttribute("teams"));
        req.setAttribute("gameStatuses", getServletContext().getAttribute("gameStatuses"));
        req.setAttribute("gameResults", getServletContext().getAttribute("gameResults"));
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_MATCHES_FIND_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String homeTeamId = req.getParameter("homeTeam");
        final String guestTeamId = req.getParameter("guestTeam");
        final String status = req.getParameter("status");
        final String result = req.getParameter("result");
        final CompletedGameFields completedGameFields = CompletedGameFields.builder()
                .isCompletedHomeTeam(!homeTeamId.equals("none"))
                .isCompletedGuestTeam(!guestTeamId.equals("none"))
                .isCompletedStatusGame(!status.equals("none"))
                .isCompletedResultGame(!result.equals("none"))
                .build();
        final GameParametersViewDto gameParametersViewDto = GameParametersViewDto.builder()
                .homeTeamId(homeTeamId)
                .guestTeamId(guestTeamId)
                .status(status)
                .result(result)
                .build();
        GameParametersControllerDto gameParametersControllerDto = gameMapper.map(gameParametersViewDto);
        final TripleListOfGameControllerDto tripleListOfGameControllerDto = gameService.selectGameByParameters(completedGameFields,
                gameParametersControllerDto);
        List<GameInProgressViewDto> listGameInProgressDto =
                tripleListOfGameControllerDto.gameViewInProgressDtoList()
                        .stream()
                        .map(gameMapper::mapProgressControllerToView)
                        .toList();
        List<GameScheduledViewDto> listGameScheduledDto =
                tripleListOfGameControllerDto.gameViewScheduledDtoList()
                        .stream()
                        .map(gameMapper::mapScheduledControllerToView)
                        .toList();
        List<GameCompletedViewDto> listGameCompletedDto =
                tripleListOfGameControllerDto.gameViewCompletedDtoList()
                        .stream()
                        .map(gameMapper::mapCompletedControllerToView)
                        .toList();
        req.setAttribute("listGameInProgressDto", listGameInProgressDto);
        req.setAttribute("listGameScheduledDto", listGameScheduledDto);
        req.setAttribute("listGameCompletedDto", listGameCompletedDto);
        doGet(req, resp);
    }
}
