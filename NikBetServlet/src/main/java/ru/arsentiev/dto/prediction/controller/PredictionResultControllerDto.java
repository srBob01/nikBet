package ru.arsentiev.dto.prediction.controller;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PredictionResultControllerDto(String homeTeam, String guestTeam, String winner, BigDecimal possibleWin) {
}
