package ru.arsentiev.dto.user.view;

import lombok.Builder;

@Builder
public record UserViewDto(Long idUser, String nickname, String firstName, String lastName, String patronymic,
                          String phoneNumber, String email, String birthDate,
                          String role) {
}
