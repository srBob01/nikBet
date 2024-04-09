package ru.arsentiev.dto.user;

import java.time.LocalDate;

public record UserEditDescriptionInfoDto(
        String firstName,
        String lastName,
        String patronymic,
        LocalDate birthDate

) {
}
