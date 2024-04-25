package ru.arsentiev.dto.prediction.controller;

import lombok.Builder;
import ru.arsentiev.entity.GameResult;

import java.math.BigDecimal;

@Builder
public record PredictionPlaceControllerDto(long idUser, long idGame, BigDecimal summa, GameResult prediction) {
}
