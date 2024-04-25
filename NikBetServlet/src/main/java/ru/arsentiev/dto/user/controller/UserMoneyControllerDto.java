package ru.arsentiev.dto.user.controller;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserMoneyControllerDto(long idUser, BigDecimal summa) {
}
