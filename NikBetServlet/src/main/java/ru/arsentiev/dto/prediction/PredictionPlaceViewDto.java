package ru.arsentiev.dto.prediction;

import lombok.Builder;

@Builder
public record PredictionPlaceViewDto(Long idUser, String idGame, String summa, String prediction) {
}
