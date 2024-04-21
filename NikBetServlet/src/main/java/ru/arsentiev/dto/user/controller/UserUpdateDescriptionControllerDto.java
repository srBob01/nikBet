package ru.arsentiev.dto.user.controller;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class UserUpdateDescriptionControllerDto {
    String idUser;
    String nickname;
    String firstName;
    String lastName;
    String patronymic;
    String phoneNumber;
    String email;
    LocalDate birthDate;
}
