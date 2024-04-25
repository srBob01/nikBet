package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.processing.validator.RegistrationUserValidator;
import ru.arsentiev.processing.validator.UpdateUserValidator;

@UtilityClass
public class ValidationManager {
    @Getter
    private static final RegistrationUserValidator registrationUserValidator;
    @Getter
    private static final UpdateUserValidator updateUserValidator;

    static {
        registrationUserValidator = new RegistrationUserValidator(DaoManager.getUserExistsDao(),
                CheckManager.getDateCheck(), CheckManager.getPasswordCheck(), CheckManager.getPhoneNumberCheck(),
                CheckManager.getNameCheck(), DateFormatterManager.getLocalDateFormatter());

        updateUserValidator = new UpdateUserValidator(DaoManager.getUserExistsDao(),
                CheckManager.getDateCheck(), CheckManager.getPhoneNumberCheck(),
                CheckManager.getNameCheck(), CheckManager.getPasswordCheck(), DateFormatterManager.getLocalDateFormatter());
    }
}
