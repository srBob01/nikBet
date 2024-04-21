package ru.arsentiev.dto.prediction.controller;

import lombok.Builder;
import ru.arsentiev.entity.GameResult;

import java.math.BigDecimal;

@Builder
public record PredictionForDeleteControllerDto(Long idPrediction, Long idGame, GameResult prediction,
                                               Float coefficient, BigDecimal summa, Long idUser) {
}
