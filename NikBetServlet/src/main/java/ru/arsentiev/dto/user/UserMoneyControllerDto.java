package ru.arsentiev.dto.user;

import java.math.BigDecimal;

public record UserMoneyControllerDto(Long idUser, BigDecimal summa) {
}
