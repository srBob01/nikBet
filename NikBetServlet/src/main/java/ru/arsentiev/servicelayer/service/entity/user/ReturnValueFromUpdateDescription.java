package ru.arsentiev.servicelayer.service.entity.user;

import lombok.Builder;
import ru.arsentiev.dto.user.controller.UserControllerDto;
import ru.arsentiev.servicelayer.validator.entity.load.LoadError;

import java.util.List;
import java.util.Optional;

@Builder
public record ReturnValueFromUpdateDescription(List<LoadError> loadErrors,
                                               Optional<UserControllerDto> userControllerDtoOptional) {
}
