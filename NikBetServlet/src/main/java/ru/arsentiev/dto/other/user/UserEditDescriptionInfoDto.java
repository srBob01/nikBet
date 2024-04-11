package ru.arsentiev.dto.other.user;

import java.time.LocalDate;

public record UserEditDescriptionInfoDto(
        String firstName,
        String lastName,
        String patronymic,
        LocalDate birthDate

) {
}
