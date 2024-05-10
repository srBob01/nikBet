package ru.arsentiev.servlet.admin.match;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.game.controller.GameAdminScheduledControllerDto;
import ru.arsentiev.dto.game.view.GameAdminScheduledViewDto;
import ru.arsentiev.dto.team.view.TeamViewDto;
import ru.arsentiev.manager.MapperManager;
import ru.arsentiev.manager.ServiceManager;
import ru.arsentiev.mapper.GameMapper;
import ru.arsentiev.mapper.TeamMapper;
import ru.arsentiev.servicelayer.service.GameService;
import ru.arsentiev.servicelayer.service.TeamService;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.JspPathGetter.ADMIN_MATCH_ADD_JSP;
import static ru.arsentiev.utils.UrlPathGetter.ADMIN_MATCH_ADD_URL;

@WebServlet(ADMIN_MATCH_ADD_URL)
public class AdminMatchAddServlet extends HttpServlet {
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
        getServletContext().setAttribute("teams", teamViewDtoList);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("teams", getServletContext().getAttribute("teams"));
        req.getRequestDispatcher(JspPathCreator.getAdminPath(ADMIN_MATCH_ADD_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String homeTeamId = req.getParameter("homeTeam");
        final String guestTeamId = req.getParameter("guestTeam");
        final String matchTime = req.getParameter("matchTime");
        final String coefficientOnHomeTeam = req.getParameter("coefficientOnHomeTeam");
        final String coefficientOnDraw = req.getParameter("coefficientOnDraw");
        final String coefficientOnGuestTeam = req.getParameter("coefficientOnGuestTeam");

        final GameAdminScheduledViewDto gameAdminScheduledViewDto = GameAdminScheduledViewDto.builder()
                .idHomeTeam(homeTeamId)
                .idGuestTeam(guestTeamId)
                .gameDate(matchTime)
                .coefficientOnHomeTeam(coefficientOnHomeTeam)
                .coefficientOnGuestTeam(coefficientOnGuestTeam)
                .coefficientOnDraw(coefficientOnDraw)
                .build();

        final GameAdminScheduledControllerDto gameAdminScheduledControllerDto = gameMapper
                .mapAdminScheduledGameViewToController(gameAdminScheduledViewDto);

        if (gameService.addNewGame(gameAdminScheduledControllerDto)) {
            req.setAttribute("messageGood", "You have added a new game");
        } else {
            req.setAttribute("messageBad", "The game has not been added. Enter all fields correctly");
        }

        doGet(req, resp);
    }
}
