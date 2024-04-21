package ru.arsentiev.dto.user.controller;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class UserRegistrationControllerDto {
    String nickname;
    String firstName;
    String lastName;
    String patronymic;
    String password;
    String phoneNumber;
    String email;
    LocalDate birthDate;
}
