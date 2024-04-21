package ru.arsentiev.dto.prediction.view;

import lombok.Builder;

@Builder
public record PredictionForDeleteViewDto(String idPrediction, String idGame, String prediction,
                                         String coefficient, String summa, Long idUser) {
}
