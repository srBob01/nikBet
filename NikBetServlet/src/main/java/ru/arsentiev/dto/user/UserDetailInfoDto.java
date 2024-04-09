package ru.arsentiev.dto.user;

import ru.arsentiev.entity.UserRole;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserDetailInfoDto(
        Long idUser,
        String nickname,
        String firstName,
        String lastName,
        String patronymic,
        String phoneNumber,
        String email,
        LocalDate birthDate,
        BigDecimal accountBalance,
        String role
) { }