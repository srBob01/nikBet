package ru.arsentiev.dto.prediction;

import ru.arsentiev.entity.GameResult;

import java.math.BigDecimal;

public record PredictionDto(String game, BigDecimal summa, String prediction) {
}
