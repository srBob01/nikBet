package ru.arsentiev.dto.user;

import java.math.BigDecimal;

public record UserBriefInfoDto(String nickname, BigDecimal accountBalance) {
}
