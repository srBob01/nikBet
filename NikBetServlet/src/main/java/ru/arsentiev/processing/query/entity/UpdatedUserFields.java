package ru.arsentiev.processing.query.entity;

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
