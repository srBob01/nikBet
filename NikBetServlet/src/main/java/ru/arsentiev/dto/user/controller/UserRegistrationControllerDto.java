package ru.arsentiev.dto.user.controller;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserRegistrationControllerDto(String nickname, String firstName, String lastName,
                                            String patronymic, String password, String phoneNumber,
                                            String email, LocalDate birthDate) {
}
