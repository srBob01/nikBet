package ru.arsentiev.dto.prediction;

import lombok.Builder;
import ru.arsentiev.entity.GameResult;

import java.math.BigDecimal;

@Builder
public record PredictionPlaceControllerDto(Long idUser, Long idGame, BigDecimal summa, GameResult prediction) {
}
