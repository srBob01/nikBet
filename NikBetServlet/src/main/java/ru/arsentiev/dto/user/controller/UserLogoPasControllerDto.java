package ru.arsentiev.dto.user.controller;

import lombok.Builder;

@Builder
public record UserLogoPasControllerDto(String email, String oldPassword, String newPassword) {
}
