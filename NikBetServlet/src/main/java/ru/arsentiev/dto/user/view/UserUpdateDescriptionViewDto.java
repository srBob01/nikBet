package ru.arsentiev.dto.user.view;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserUpdateDescriptionViewDto {
    String idUser;
    String nickname;
    String firstName;
    String lastName;
    String patronymic;
    String phoneNumber;
    String email;
    String birthDate;
}
