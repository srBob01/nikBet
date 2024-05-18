package ru.arsentiev.dto.user.view;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserValidatorViewDto {
    String nickname;
    String firstName;
    String lastName;
    String patronymic;
    String password;
    String phoneNumber;
    String email;
    String birthDate;
}