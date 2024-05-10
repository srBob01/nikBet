package ru.arsentiev.servlet.user.matches;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.prediction.controller.PredictionPlaceControllerDto;
import ru.arsentiev.dto.prediction.view.PredictionPlaceViewDto;
import ru.arsentiev.dto.prediction.controller.PredictionResultControllerDto;
import ru.arsentiev.dto.prediction.view.PredictionResultViewDto;
import ru.arsentiev.dto.user.controller.UserControllerDto;
import ru.arsentiev.dto.user.controller.UserMoneyControllerDto;
import ru.arsentiev.dto.user.view.UserMoneyViewDto;
import ru.arsentiev.manager.MapperManager;
import ru.arsentiev.manager.ServiceManager;
import ru.arsentiev.mapper.GameMapper;
import ru.arsentiev.mapper.PredictionMapper;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.servicelayer.service.GameService;
import ru.arsentiev.servicelayer.service.PredictionService;
import ru.arsentiev.servicelayer.service.UserService;

import java.io.IOException;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_ERROR;
import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;

public class UserMatchesBaseServlet extends HttpServlet {
    private UserService userService;
    private PredictionService predictionService;
    protected GameService gameService;
    protected GameMapper gameMapper;
    private UserMapper userMapper;
    private PredictionMapper predictionMapper;

    @Override
    public void init() throws ServletException {
        predictionMapper = MapperManager.getPredictionMapper();
        userMapper = MapperManager.getUserMapper();
        userService = ServiceManager.getUserService();
        gameService = ServiceManager.getGameService();
        predictionService = ServiceManager.getPredictionService();
        gameMapper = MapperManager.getGameMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idGame = req.getParameter("gameId");
        String betType = req.getParameter("betType");
        String betAmount = req.getParameter("betAmount");
        UserControllerDto user = (UserControllerDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        UserMoneyViewDto userMoneyViewDto = UserMoneyViewDto.builder()
                .idUser(user.idUser())
                .summa(betAmount)
                .build();
        UserMoneyControllerDto userMoneyControllerDto = userMapper.map(userMoneyViewDto);
        if (!userService.withdrawMoney(userMoneyControllerDto)) {
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

            PredictionPlaceControllerDto predictionPlaceControllerDto =
                    predictionMapper.map(predictionPlaceViewDto);

            PredictionResultControllerDto predictionResultControllerDto =
                    predictionService.insertPrediction(predictionPlaceControllerDto);

            PredictionResultViewDto predictionResultViewDto =
                    predictionMapper.map(predictionResultControllerDto);

            req.setAttribute("predictionResultViewDto", predictionResultViewDto);
        }
        doGet(req, resp);
    }
}
