package ru.arsentiev.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.prediction.PredictionPlaceControllerDto;
import ru.arsentiev.dto.prediction.PredictionPlaceViewDto;
import ru.arsentiev.dto.prediction.PredictionResultDto;
import ru.arsentiev.dto.prediction.PredictionViewDto;
import ru.arsentiev.entity.GameResult;
import ru.arsentiev.entity.Prediction;
import ru.arsentiev.manager.DaoManager;
import ru.arsentiev.mapper.PredictionMapper;
import ru.arsentiev.repository.PredictionDao;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PredictionService {
    private static final PredictionService INSTANCE = new PredictionService();
    private final PredictionMapper predictionMapper = PredictionMapper.getInstance();
    private final PredictionDao predictionDao = DaoManager.getPredictionDao();

    public static PredictionService getInstance() {
        return INSTANCE;
    }

    public void insertPrediction(PredictionPlaceViewDto predictionPlaceViewDto) {
        PredictionPlaceControllerDto predictionPlaceControllerDto = predictionMapper.map(predictionPlaceViewDto);
        Prediction prediction = predictionMapper.map(predictionPlaceControllerDto);
        predictionDao.insert(prediction);
    }

    public PredictionResultDto getResultPredictionDto(PredictionViewDto predictionViewDto) {
        GameResult prediction = GameResult.valueOf(predictionViewDto.prediction());
        BigDecimal summa = BigDecimal.valueOf(Double.parseDouble(predictionViewDto.summa()));
        BigDecimal possibleWin = switch (prediction) {
            case HomeWin -> BigDecimal.valueOf(Double.parseDouble(predictionViewDto.coefficientOnHomeTeam()))
                    .multiply(summa);
            case AwayWin -> BigDecimal.valueOf(Double.parseDouble(predictionViewDto.coefficientOnGuestTeam()))
                    .multiply(summa);
            case Draw -> BigDecimal.valueOf(Double.parseDouble(predictionViewDto.coefficientOnDraw()))
                    .multiply(summa);
        };
        String winner = switch (prediction) {
            case HomeWin -> predictionViewDto.homeTeam();
            case AwayWin -> predictionViewDto.guestTeam();
            case Draw -> "Draw";
        };
        return PredictionResultDto.builder()
                .homeTeam(predictionViewDto.homeTeam())
                .guestTeam(predictionViewDto.guestTeam())
                .possibleWin(possibleWin)
                .winner(winner)
                .build();
    }
}
