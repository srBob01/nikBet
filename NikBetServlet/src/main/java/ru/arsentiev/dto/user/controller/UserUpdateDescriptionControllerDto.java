package ru.arsentiev.dto.user.controller;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserUpdateDescriptionControllerDto(long idUser, String nickname, String firstName,
                                                 String lastName, String patronymic, String phoneNumber,
                                                 String email, LocalDate birthDate) {
}

