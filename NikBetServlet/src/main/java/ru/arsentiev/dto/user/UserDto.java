package ru.arsentiev.dto.user;

import lombok.Builder;
import ru.arsentiev.entity.UserRole;

import java.time.LocalDate;

@Builder
public record UserDto(Long idUser, String nickname, String firstName, String lastName, String patronymic,
                      String phoneNumber, String email, LocalDate birthDate,
                      UserRole role) {
}