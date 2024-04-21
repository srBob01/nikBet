package ru.arsentiev.dto.user.view;

import lombok.Builder;

@Builder
public record UserLogoPasViewDto(String email, String oldPassword, String newPassword) {
}
