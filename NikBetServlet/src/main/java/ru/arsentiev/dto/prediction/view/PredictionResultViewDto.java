package ru.arsentiev.dto.prediction.view;

import lombok.Builder;

@Builder
public record PredictionResultViewDto(String homeTeam, String guestTeam, String winner, String possibleWin) {
}
