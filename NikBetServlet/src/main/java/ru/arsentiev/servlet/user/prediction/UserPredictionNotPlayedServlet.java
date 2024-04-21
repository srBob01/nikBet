package ru.arsentiev.servlet.user.prediction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.prediction.view.PredictionNotPlayedViewDto;
import ru.arsentiev.dto.user.controller.UserControllerDto;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.JspPathGetter.USER_PREDICTION_NOT_PLAYED_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_PREDICTION_NOT_PLAYED_URL;

@WebServlet(USER_PREDICTION_NOT_PLAYED_URL)
public class UserPredictionNotPlayedServlet extends UserPredictionBaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserControllerDto user = (UserControllerDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        Long idUser = user.idUser();

        List<PredictionNotPlayedViewDto> predictionBetNotPlayedViewDtoList =
                predictionService.selectBetNotPlayedPredictions(idUser)
                        .stream()
                        .map(predictionMapper::map)
                        .toList();

        req.setAttribute("predictionBetNotPlayedViewDtoList", predictionBetNotPlayedViewDtoList);

        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_PREDICTION_NOT_PLAYED_JSP)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
