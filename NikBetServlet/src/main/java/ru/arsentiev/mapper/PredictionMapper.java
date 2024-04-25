package ru.arsentiev.mapper;

import ru.arsentiev.dto.prediction.controller.*;
import ru.arsentiev.dto.prediction.view.*;
import ru.arsentiev.entity.Game;
import ru.arsentiev.entity.GameResult;
import ru.arsentiev.entity.Prediction;
import ru.arsentiev.entity.User;
import ru.arsentiev.processing.dateformatter.TimeStampFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PredictionMapper {
    private final TimeStampFormatter timeStampFormatter;

    public PredictionMapper(TimeStampFormatter timeStampFormatter) {
        this.timeStampFormatter = timeStampFormatter;
    }

    public PredictionPlaceControllerDto map(PredictionPlaceViewDto predictionPlaceViewDto) {
        long idGameAsLong = Long.parseLong(predictionPlaceViewDto.idGame());
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
                .possibleWin(predictionResultControllerDto.possibleWin().setScale(2,
                        RoundingMode.HALF_UP).toString())
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
                .goalHomeTeam(String.valueOf(dto.goalHomeTeam()))
                .goalGuestTeam(String.valueOf(dto.goalGuestTeam()))
                .predictionDate(dto.predictionDate() != null ?
                        dto.predictionDate().format(timeStampFormatter.FORMATTER) : "")
                .summa(dto.summa() != null ? dto.summa().toString() : "")
                .prediction(dto.prediction() != null ? dto.prediction().name() : "")
                .predictionStatus(dto.predictionStatus() != null ? dto.predictionStatus().name() : "")
                .coefficient(String.valueOf(dto.coefficient()))
                .build();
    }

    public PredictionPlayedViewDto map(PredictionPlayedControllerDto dto) {
        return PredictionPlayedViewDto.builder()
                .idPrediction(String.valueOf(dto.idPrediction()))
                .idGame(String.valueOf(dto.idGame()))
                .homeTeam(dto.homeTeam())
                .guestTeam(dto.guestTeam())
                .goalHomeTeam(String.valueOf(dto.goalHomeTeam()))
                .goalGuestTeam(String.valueOf(dto.goalGuestTeam()))
                .predictionDate(dto.predictionDate() != null ?
                        dto.predictionDate().format(timeStampFormatter.FORMATTER) : "")
                .summa(dto.summa() != null ? dto.summa().toString() : "")
                .prediction(dto.prediction() != null ? dto.prediction().name() : "")
                .result(dto.result().name())
                .predictionStatus(dto.predictionStatus() != null ? dto.predictionStatus().name() : "")
                .coefficient(String.valueOf(dto.coefficient()))
                .build();
    }

    public PredictionForDeleteControllerDto map(PredictionForDeleteViewDto predictionForDeleteViewDto) {
        return PredictionForDeleteControllerDto.builder()
                .idUser(predictionForDeleteViewDto.idUser())
                .coefficient(Float.parseFloat(predictionForDeleteViewDto.coefficient().trim()))
                .summa(BigDecimal.valueOf(Float.parseFloat(predictionForDeleteViewDto.summa().trim())))
                .idGame(Long.parseLong(predictionForDeleteViewDto.idGame().trim()))
                .prediction(GameResult.valueOf(predictionForDeleteViewDto.prediction().trim()))
                .idPrediction(Long.parseLong(predictionForDeleteViewDto.idPrediction().trim()))
                .build();
    }
}