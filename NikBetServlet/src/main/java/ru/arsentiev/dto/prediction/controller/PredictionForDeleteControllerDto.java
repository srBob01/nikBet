package ru.arsentiev.dto.prediction.controller;

import lombok.Builder;
import ru.arsentiev.entity.GameResult;

import java.math.BigDecimal;

@Builder
public record PredictionForDeleteControllerDto(long idPrediction, long idGame, GameResult prediction,
                                               float coefficient, BigDecimal summa, long idUser) {
}
