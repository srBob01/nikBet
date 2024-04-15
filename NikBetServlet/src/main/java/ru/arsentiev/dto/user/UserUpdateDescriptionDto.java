package ru.arsentiev.dto.user;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserUpdateDescriptionDto {
    String idUser;
    String nickname;
    String firstName;
    String lastName;
    String patronymic;
    String phoneNumber;
    String email;
    String birthDate;
}
