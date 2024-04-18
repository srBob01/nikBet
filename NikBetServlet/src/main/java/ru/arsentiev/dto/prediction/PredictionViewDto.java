package ru.arsentiev.dto.prediction;

import lombok.Builder;

@Builder
public record PredictionViewDto(String homeTeam, String guestTeam, String prediction,
                                String coefficientOnHomeTeam, String coefficientOnDraw,
                                String coefficientOnGuestTeam, String summa) {
}
