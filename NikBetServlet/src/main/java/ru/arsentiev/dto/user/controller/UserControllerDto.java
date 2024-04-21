package ru.arsentiev.dto.user.controller;

import lombok.Builder;
import ru.arsentiev.entity.UserRole;

import java.time.LocalDate;

@Builder
public record UserControllerDto(Long idUser, String nickname, String firstName, String lastName, String patronymic,
                                String phoneNumber, String email, LocalDate birthDate,
                                UserRole role) {
}