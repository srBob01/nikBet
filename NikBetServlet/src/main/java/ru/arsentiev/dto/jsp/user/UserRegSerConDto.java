package ru.arsentiev.dto.jsp.user;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserRegSerConDto {
    String nickname;
    String firstName;
    String lastName;
    String patronymic;
    String password;
    String phoneNumber;
    String email;
    String birthDate;
}
