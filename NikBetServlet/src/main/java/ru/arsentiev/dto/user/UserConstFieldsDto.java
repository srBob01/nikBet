package ru.arsentiev.dto.user;

import ru.arsentiev.entity.UserRole;

public record UserConstFieldsDto(Long idUser, UserRole role) {
}
