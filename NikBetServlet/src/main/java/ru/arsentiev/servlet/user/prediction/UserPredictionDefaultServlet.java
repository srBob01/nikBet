package ru.arsentiev.servlet.user.prediction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.prediction.view.PredictionNotPlayedViewDto;
import ru.arsentiev.dto.prediction.view.PredictionPlayedViewDto;
import ru.arsentiev.dto.user.controller.UserControllerDto;
import ru.arsentiev.servicelayer.service.entity.prediction.DoubleListPredictionControllerDto;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.JspPathGetter.USER_PREDICTION_DEFAULT_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_PREDICTION_DEFAULT_URL;

@WebServlet(USER_PREDICTION_DEFAULT_URL)
public class UserPredictionDefaultServlet extends UserPredictionBaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserControllerDto user = (UserControllerDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        Long idUser = user.idUser();
        DoubleListPredictionControllerDto doubleListPredictionControllerDto = predictionService
                .selectDoublePredictionsList(idUser);
        List<PredictionNotPlayedViewDto> predictionBetNotPlayedViewDtoList = doubleListPredictionControllerDto
                .predictionControllerBetNotPlayedDtoList()
                .stream()
                .map(predictionMapper::map)
                .toList();
        List<PredictionPlayedViewDto> predictionBetPlayedViewDtoList = doubleListPredictionControllerDto
                .predictionControllerBetPlayedDtoList()
                .stream()
                .map(predictionMapper::map)
                .toList();

        req.setAttribute("predictionBetNotPlayedViewDtoList", predictionBetNotPlayedViewDtoList);
        req.setAttribute("predictionBetPlayedViewDtoList", predictionBetPlayedViewDtoList);

        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_PREDICTION_DEFAULT_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
