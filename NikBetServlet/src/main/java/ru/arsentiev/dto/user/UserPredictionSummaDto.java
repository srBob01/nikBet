package ru.arsentiev.dto.user;

import lombok.Builder;

@Builder
public record UserPredictionSummaDto(Long idUser, String summa) {
}
