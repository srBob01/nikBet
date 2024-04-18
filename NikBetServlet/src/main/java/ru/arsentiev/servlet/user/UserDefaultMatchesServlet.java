package ru.arsentiev.servlet.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.game.GameViewCompletedDto;
import ru.arsentiev.dto.game.GameViewInProgressDto;
import ru.arsentiev.dto.game.GameViewScheduledDto;
import ru.arsentiev.dto.prediction.PredictionPlaceViewDto;
import ru.arsentiev.dto.prediction.PredictionResultDto;
import ru.arsentiev.dto.prediction.PredictionViewDto;
import ru.arsentiev.dto.user.UserDto;
import ru.arsentiev.dto.user.UserPredictionSummaDto;
import ru.arsentiev.service.GameService;
import ru.arsentiev.service.PredictionService;
import ru.arsentiev.service.UserService;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_ERROR;
import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.JspPathGetter.USER_MATCHES_DEFAULT_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_MATCHES_DEFAULT_URL;

@WebServlet(USER_MATCHES_DEFAULT_URL)
public class UserDefaultMatchesServlet extends HttpServlet {
    private static UserService userService;
    private static GameService gameService;
    private static PredictionService predictionService;


    @Override
    public void init() throws ServletException {
        userService = UserService.getInstance();
        gameService = GameService.getInstance();
        predictionService = PredictionService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<GameViewInProgressDto> listGameInProgressDto = gameService.selectGameInProgress();
        List<GameViewScheduledDto> listGameScheduledDto = gameService.selectGameScheduled();
        List<GameViewCompletedDto> listGameCompletedDto = gameService.selectGameCompleted();
        req.setAttribute("listGameInProgressDto", listGameInProgressDto);
        req.setAttribute("listGameScheduledDto", listGameScheduledDto);
        req.setAttribute("listGameCompletedDto", listGameCompletedDto);
        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_MATCHES_DEFAULT_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idGame = req.getParameter("gameId");
        String betType = req.getParameter("betType");
        String betAmount = req.getParameter("betAmount");
        UserDto user = (UserDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        UserPredictionSummaDto userPredictionSummaDto = UserPredictionSummaDto.builder()
                .idUser(user.idUser())
                .summa(betAmount)
                .build();
        if (!userService.checkSummaPrediction(userPredictionSummaDto)) {
            req.removeAttribute("predictionResultDto");
            req.setAttribute(NAME_ATTRIBUTE_ERROR, "Not enough money");
            doGet(req, resp);
        } else {
            req.removeAttribute(NAME_ATTRIBUTE_ERROR);
            PredictionPlaceViewDto predictionPlaceViewDto = PredictionPlaceViewDto.builder()
                    .idUser(user.idUser())
                    .idGame(idGame)
                    .summa(betAmount)
                    .prediction(betType)
                    .build();
            predictionService.insertPrediction(predictionPlaceViewDto);
            PredictionViewDto predictionViewDto = PredictionViewDto.builder()
                    .homeTeam(req.getParameter("homeTeam"))
                    .guestTeam(req.getParameter("guestTeam"))
                    .prediction(betType)
                    .coefficientOnHomeTeam(req.getParameter("coefficientOnHomeTeam"))
                    .coefficientOnDraw(req.getParameter("coefficientOnDraw"))
                    .coefficientOnGuestTeam(req.getParameter("coefficientOnGuestTeam"))
                    .summa(betAmount)
                    .build();
            PredictionResultDto predictionResultDto = predictionService.getResultPredictionDto(predictionViewDto);
            req.setAttribute("predictionResultDto", predictionResultDto);
            doGet(req, resp);
        }
    }
}
