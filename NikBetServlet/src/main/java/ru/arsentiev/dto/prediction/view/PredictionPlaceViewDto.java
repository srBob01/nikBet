package ru.arsentiev.dto.prediction.view;

import lombok.Builder;

@Builder
public record PredictionPlaceViewDto(Long idUser, String idGame, String summa, String prediction) {
}
