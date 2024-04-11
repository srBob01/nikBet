package ru.arsentiev.dto.other.prediction;

import java.math.BigDecimal;

public record PredictionDto(String game, BigDecimal summa, String prediction) {
}
