package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.servicelayer.validator.UserValidator;

@UtilityClass
public class ValidationManager {
    @Getter
    private static final UserValidator userValidator;

    static {
        userValidator = new UserValidator(CheckManager.getDateCheck(), CheckManager.getPasswordCheck(), CheckManager.getPhoneNumberCheck(),
                CheckManager.getEmptyCheck(), CheckManager.getNameCheck(), DateFormatterManager.getLocalDateFormatter());
    }
}
