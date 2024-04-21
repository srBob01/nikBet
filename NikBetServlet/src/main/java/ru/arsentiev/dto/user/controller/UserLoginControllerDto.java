package ru.arsentiev.dto.user.controller;

import lombok.Builder;

@Builder
public record UserLoginControllerDto(String email, String password) {
}
