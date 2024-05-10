package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.servicelayer.validator.RegistrationUserValidator;
import ru.arsentiev.servicelayer.validator.UpdateUserValidator;

@UtilityClass
public class ValidationManager {
    @Getter
    private static final RegistrationUserValidator registrationUserValidator;
    @Getter
    private static final UpdateUserValidator updateUserValidator;

    static {
        registrationUserValidator = new RegistrationUserValidator(RepositoryManager.getUserExistRepository(),
                CheckManager.getDateCheck(), CheckManager.getPasswordCheck(), CheckManager.getPhoneNumberCheck(),
                CheckManager.getNameCheck(), DateFormatterManager.getLocalDateFormatter());

        updateUserValidator = new UpdateUserValidator(RepositoryManager.getUserExistRepository(),
                CheckManager.getDateCheck(), CheckManager.getPhoneNumberCheck(),
                CheckManager.getNameCheck(), CheckManager.getPasswordCheck(), DateFormatterManager.getLocalDateFormatter());
    }
}
