package ru.arsentiev.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.prediction.PredictionPlaceControllerDto;
import ru.arsentiev.dto.prediction.PredictionPlaceViewDto;
import ru.arsentiev.entity.Game;
import ru.arsentiev.entity.GameResult;
import ru.arsentiev.entity.Prediction;
import ru.arsentiev.entity.User;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PredictionMapper {
    private static final PredictionMapper INSTANCE = new PredictionMapper();

    public static PredictionMapper getInstance() {
        return INSTANCE;
    }

    public PredictionPlaceControllerDto map(PredictionPlaceViewDto predictionPlaceViewDto) {
        Long idGameAsLong = Long.parseLong(predictionPlaceViewDto.idGame());
        BigDecimal summaAsBigDecimal = new BigDecimal(predictionPlaceViewDto.summa());
        GameResult predictionAsEnum = GameResult.valueOf(predictionPlaceViewDto.prediction());

        return PredictionPlaceControllerDto.builder()
                .idUser(predictionPlaceViewDto.idUser())
                .idGame(idGameAsLong)
                .summa(summaAsBigDecimal)
                .prediction(predictionAsEnum)
                .build();
    }

    public Prediction map(PredictionPlaceControllerDto predictionPlaceControllerDto) {
        return Prediction.builder()
                .user(User.builder()
                        .idUser(predictionPlaceControllerDto.idUser())
                        .build())
                .game(Game.builder()
                        .idGame(predictionPlaceControllerDto.idGame())
                        .build())
                .prediction(predictionPlaceControllerDto.prediction())
                .summa(predictionPlaceControllerDto.summa())
                .build();
    }
}
