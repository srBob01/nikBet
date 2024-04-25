package ru.arsentiev.dto.user.controller;

import lombok.Builder;
import ru.arsentiev.entity.UserRole;

@Builder
public record UserConstFieldsControllerDto(long idUser, UserRole role) {
}
