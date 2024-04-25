package ru.arsentiev.dto.user.controller;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record UserForAdminControllerDto(long idUser, String nickname, String firstName, String lastName,
                                        String patronymic, String phoneNumber, String email, LocalDate birthDate,
                                        BigDecimal accountBalance) {
}
