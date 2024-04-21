package ru.arsentiev.dto.user.view;

import lombok.Builder;

@Builder
public record UserLoginViewDto(String email, String password) {
}
