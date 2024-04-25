package ru.arsentiev.dto.prediction.controller;

import lombok.Builder;
import ru.arsentiev.entity.GameResult;
import ru.arsentiev.entity.PredictionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PredictionNotPlayedControllerDto(long idPrediction,
                                               long idGame,
                                               String homeTeam,
                                               String guestTeam,
                                               int goalHomeTeam,
                                               int goalGuestTeam,
                                               LocalDateTime predictionDate,
                                               BigDecimal summa,
                                               GameResult prediction,
                                               PredictionStatus predictionStatus,
                                               float coefficient) {
}
