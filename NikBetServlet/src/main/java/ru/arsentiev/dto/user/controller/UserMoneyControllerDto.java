package ru.arsentiev.dto.user.controller;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserMoneyControllerDto(Long idUser, BigDecimal summa) {
}
