package ru.arsentiev.dto.prediction.view;

import lombok.Builder;

@Builder
public record PredictionNotPlayedViewDto(String idPrediction,
                                         String idGame,
                                         String homeTeam,
                                         String guestTeam,
                                         String goalHomeTeam,
                                         String goalGuestTeam,
                                         String predictionDate,
                                         String summa,
                                         String prediction,
                                         String predictionStatus,
                                         String coefficient) {
}
