package ru.arsentiev.service.entity.user;

import ru.arsentiev.dto.user.UserDto;
import ru.arsentiev.validator.entity.load.LoadError;

import java.util.List;
import java.util.Optional;

public record ReturnValueOnUpdateDescription(List<LoadError> errors, UserDto userDto) {
}
