package ru.arsentiev.servlet.user.prediction;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.prediction.controller.PredictionForDeleteControllerDto;
import ru.arsentiev.dto.prediction.view.PredictionForDeleteViewDto;
import ru.arsentiev.dto.user.controller.UserControllerDto;
import ru.arsentiev.manager.MapperManager;
import ru.arsentiev.manager.ServiceManager;
import ru.arsentiev.mapper.PredictionMapper;
import ru.arsentiev.service.PredictionService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_ERROR;
import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;

public class UserPredictionBaseServlet extends HttpServlet {
    protected PredictionService predictionService;
    protected PredictionMapper predictionMapper;

    @Override
    public void init() throws ServletException {
        predictionService = ServiceManager.getPredictionService();
        predictionMapper = MapperManager.getPredictionMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserControllerDto user = (UserControllerDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        String idPrediction = req.getParameter("idPrediction");
        String summa = req.getParameter("summa");
        String coefficient = req.getParameter("coefficient");
        String idGame = req.getParameter("idGame");
        String prediction = req.getParameter("prediction");
        PredictionForDeleteViewDto predictionForDeleteViewDto = PredictionForDeleteViewDto.builder()
                .idPrediction(idPrediction)
                .coefficient(coefficient)
                .prediction(prediction)
                .summa(summa)
                .idGame(idGame)
                .idUser(user.idUser())
                .build();

        PredictionForDeleteControllerDto predictionForDeleteControllerDto =
                predictionMapper.map(predictionForDeleteViewDto);

        Optional<BigDecimal> refund = predictionService.deletePrediction(predictionForDeleteControllerDto);
        if (refund.isEmpty()) {
            req.setAttribute(NAME_ATTRIBUTE_ERROR, "The bet cannot be deleted");

        } else {
            req.setAttribute("refund", refund.get().toString());
        }
        doGet(req, resp);
    }
}
