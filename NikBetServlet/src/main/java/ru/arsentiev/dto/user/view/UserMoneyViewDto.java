package ru.arsentiev.dto.user.view;

import lombok.Builder;

@Builder
public record UserMoneyViewDto(Long idUser, String summa) {
}
