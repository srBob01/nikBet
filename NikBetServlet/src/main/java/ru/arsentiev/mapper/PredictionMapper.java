package ru.arsentiev.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.prediction.controller.*;
import ru.arsentiev.dto.prediction.view.*;
import ru.arsentiev.entity.Game;
import ru.arsentiev.entity.GameResult;
import ru.arsentiev.entity.Prediction;
import ru.arsentiev.entity.User;
import ru.arsentiev.utils.TimeStampFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public PredictionResultViewDto map(PredictionResultControllerDto predictionResultControllerDto) {
        return PredictionResultViewDto.builder()
                .possibleWin(predictionResultControllerDto.possibleWin().setScale(2, RoundingMode.HALF_UP).toString())
                .winner(predictionResultControllerDto.winner())
                .guestTeam(predictionResultControllerDto.guestTeam())
                .homeTeam(predictionResultControllerDto.homeTeam())
                .build();
    }

    public PredictionPlayedControllerDto mapPredictionPlayed(Prediction prediction) {
        return PredictionPlayedControllerDto.builder()
                .idPrediction(prediction.getIdPrediction())
                .idGame(prediction.getGame().getIdGame())
                .homeTeam(prediction.getGame().getHomeTeam().getTitle())
                .goalHomeTeam(prediction.getGame().getGoalHomeTeam())
                .guestTeam(prediction.getGame().getGuestTeam().getTitle())
                .goalGuestTeam(prediction.getGame().getGoalGuestTeam())
                .predictionDate(prediction.getPredictionDate())
                .summa(prediction.getSumma())
                .prediction(prediction.getPrediction())
                .result(prediction.getGame().getResult())
                .predictionStatus(prediction.getPredictionStatus())
                .coefficient(prediction.getCoefficient())
                .build();
    }

    public PredictionNotPlayedControllerDto mapPredictionNotPlayed(Prediction prediction) {
        return PredictionNotPlayedControllerDto.builder()
                .idPrediction(prediction.getIdPrediction())
                .idGame(prediction.getGame().getIdGame())
                .homeTeam(prediction.getGame().getHomeTeam().getTitle())
                .goalHomeTeam(prediction.getGame().getGoalHomeTeam())
                .guestTeam(prediction.getGame().getGuestTeam().getTitle())
                .goalGuestTeam(prediction.getGame().getGoalGuestTeam())
                .predictionDate(prediction.getPredictionDate())
                .summa(prediction.getSumma())
                .prediction(prediction.getPrediction())
                .predictionStatus(prediction.getPredictionStatus())
                .coefficient(prediction.getCoefficient())
                .build();
    }

    public PredictionNotPlayedViewDto map(PredictionNotPlayedControllerDto dto) {
        return PredictionNotPlayedViewDto.builder()
                .idPrediction(String.valueOf(dto.idPrediction()))
                .idGame(String.valueOf(dto.idGame()))
                .homeTeam(dto.homeTeam())
                .guestTeam(dto.guestTeam())
                .goalHomeTeam(dto.goalHomeTeam() != null ? dto.goalHomeTeam().toString() : "")
                .goalGuestTeam(dto.goalGuestTeam() != null ? dto.goalGuestTeam().toString() : "")
                .predictionDate(dto.predictionDate() != null ? dto.predictionDate().format(TimeStampFormatter.FORMATTER) : "")
                .summa(dto.summa() != null ? dto.summa().toString() : "")
                .prediction(dto.prediction() != null ? dto.prediction().name() : "")
                .predictionStatus(dto.predictionStatus() != null ? dto.predictionStatus().name() : "")
                .coefficient(dto.coefficient() != null ? String.valueOf(dto.coefficient()) : "")
                .build();
    }

    public PredictionPlayedViewDto map(PredictionPlayedControllerDto dto) {
        return PredictionPlayedViewDto.builder()
                .idPrediction(String.valueOf(dto.idPrediction()))
                .idGame(String.valueOf(dto.idGame()))
                .homeTeam(dto.homeTeam())
                .guestTeam(dto.guestTeam())
                .goalHomeTeam(dto.goalHomeTeam() != null ? dto.goalHomeTeam().toString() : "")
                .goalGuestTeam(dto.goalGuestTeam() != null ? dto.goalGuestTeam().toString() : "")
                .predictionDate(dto.predictionDate() != null ? dto.predictionDate().format(TimeStampFormatter.FORMATTER) : "")
                .summa(dto.summa() != null ? dto.summa().toString() : "")
                .prediction(dto.prediction() != null ? dto.prediction().name() : "")
                .result(dto.result().name())
                .predictionStatus(dto.predictionStatus() != null ? dto.predictionStatus().name() : "")
                .coefficient(dto.coefficient() != null ? String.valueOf(dto.coefficient()) : "")
                .build();
    }

    public PredictionForDeleteControllerDto map(PredictionForDeleteViewDto predictionForDeleteViewDto) {
        return PredictionForDeleteControllerDto.builder()
                .idUser(predictionForDeleteViewDto.idUser())
                .coefficient(Float.parseFloat(predictionForDeleteViewDto.coefficient().trim()))
                .summa(BigDecimal.valueOf(Float.parseFloat(predictionForDeleteViewDto.summa().trim())))
                .idGame(Long.valueOf(predictionForDeleteViewDto.idGame().trim()))
                .prediction(GameResult.valueOf(predictionForDeleteViewDto.prediction().trim()))
                .idPrediction(Long.valueOf(predictionForDeleteViewDto.idPrediction().trim()))
                .build();
    }
}
