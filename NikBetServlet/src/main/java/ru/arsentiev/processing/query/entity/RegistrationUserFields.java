package ru.arsentiev.processing.query.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class RegistrationUserFields {
    private UpdatedUserFields updatedUserFields;
    private boolean isNewPassword;
}
