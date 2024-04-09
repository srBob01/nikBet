package ru.arsentiev.dto.user;

public record UserEditPasswordDto(String oldPassword, String newPassword) {
}
