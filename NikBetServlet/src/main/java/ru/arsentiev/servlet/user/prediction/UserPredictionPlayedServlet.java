package ru.arsentiev.servlet.user.prediction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.arsentiev.dto.prediction.view.PredictionPlayedViewDto;
import ru.arsentiev.dto.user.controller.UserControllerDto;
import ru.arsentiev.manager.MapperManager;
import ru.arsentiev.manager.ServiceManager;
import ru.arsentiev.mapper.PredictionMapper;
import ru.arsentiev.service.PredictionService;
import ru.arsentiev.utils.JspPathCreator;

import java.io.IOException;
import java.util.List;

import static ru.arsentiev.utils.AttributeGetter.NAME_ATTRIBUTE_USER;
import static ru.arsentiev.utils.JspPathGetter.USER_PREDICTION_PLAYED_JSP;
import static ru.arsentiev.utils.UrlPathGetter.USER_PREDICTION_PLAYED_URL;

@WebServlet(USER_PREDICTION_PLAYED_URL)
public class UserPredictionPlayedServlet extends HttpServlet {
    protected PredictionService predictionService;
    protected PredictionMapper predictionMapper;

    @Override
    public void init() throws ServletException {
        predictionService = ServiceManager.getPredictionService();
        predictionMapper = MapperManager.getPredictionMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserControllerDto user = (UserControllerDto) req.getSession().getAttribute(NAME_ATTRIBUTE_USER);
        Long idUser = user.idUser();

        List<PredictionPlayedViewDto> predictionBetPlayedViewDtoList =
                predictionService.selectBetPlayedPredictions(idUser)
                        .stream()
                        .map(predictionMapper::map)
                        .toList();

        req.setAttribute("predictionBetPlayedViewDtoList", predictionBetPlayedViewDtoList);

        req.getRequestDispatcher(JspPathCreator.getUserPath(USER_PREDICTION_PLAYED_JSP)).forward(req, resp);
    }
}
