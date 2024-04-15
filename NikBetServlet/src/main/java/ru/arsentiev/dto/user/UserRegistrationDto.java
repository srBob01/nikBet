package ru.arsentiev.dto.user;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserRegistrationDto {
    String nickname;
    String firstName;
    String lastName;
    String patronymic;
    String password;
    String phoneNumber;
    String email;
    String birthDate;
}
