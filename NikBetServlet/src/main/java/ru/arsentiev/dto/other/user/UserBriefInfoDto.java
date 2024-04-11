package ru.arsentiev.dto.other.user;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserBriefInfoDto(String nickname, BigDecimal accountBalance) {
}
