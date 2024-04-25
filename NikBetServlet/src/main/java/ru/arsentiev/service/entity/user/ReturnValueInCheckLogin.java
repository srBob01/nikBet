package ru.arsentiev.service.entity.user;

import ru.arsentiev.dto.user.controller.UserControllerDto;
import ru.arsentiev.processing.validator.entity.login.LoginError;

import java.util.Optional;

public record ReturnValueInCheckLogin(LoginError loginError, Optional<UserControllerDto> userDto) {
}
