package ru.arsentiev.dto.prediction;

import lombok.Builder;
import ru.arsentiev.entity.GameResult;

import java.math.BigDecimal;

@Builder
public record PredictionResultDto(String homeTeam, String guestTeam, String winner, BigDecimal  possibleWin) {
}
