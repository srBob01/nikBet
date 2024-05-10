package ru.arsentiev.dto.user.controller;

import lombok.Builder;

@Builder
public record UserPasswordAndSaltControllerDto(String salt, String password) {
}
