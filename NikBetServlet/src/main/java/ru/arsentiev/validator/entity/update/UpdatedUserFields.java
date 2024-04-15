package ru.arsentiev.validator.entity.update;

import lombok.Builder;

@Builder
public record UpdatedUserFields(boolean isNewNickName,
                                boolean isNewFirstName,
                                boolean isNewLastName,
                                boolean isNewPatronymic,
                                boolean isNewEmail,
                                boolean isNewPhoneNumber,
                                boolean isNewBirthDate) {
}
