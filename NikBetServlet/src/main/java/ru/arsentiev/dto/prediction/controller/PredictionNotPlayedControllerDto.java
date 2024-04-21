package ru.arsentiev.dto.prediction.controller;

import lombok.Builder;
import ru.arsentiev.entity.GameResult;
import ru.arsentiev.entity.PredictionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PredictionNotPlayedControllerDto(Long idPrediction,
                                               Long idGame,
                                               String homeTeam,
                                               String guestTeam,
                                               Integer goalHomeTeam,
                                               Integer goalGuestTeam,
                                               LocalDateTime predictionDate,
                                               BigDecimal summa,
                                               GameResult prediction,
                                               PredictionStatus predictionStatus,
                                               Float coefficient) {
}
