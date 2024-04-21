package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.singleton.check.DateCheck;
import ru.arsentiev.singleton.check.NameCheck;
import ru.arsentiev.singleton.check.PasswordCheck;
import ru.arsentiev.singleton.check.PhoneNumberCheck;
import ru.arsentiev.validator.RegistrationUserValidator;
import ru.arsentiev.validator.UpdateUserValidator;

@UtilityClass
public class ValidationManager {
    @Getter
    private static final RegistrationUserValidator registrationUserValidator;
    @Getter
    private static final UpdateUserValidator updateUserValidator;

    static {
        registrationUserValidator = new RegistrationUserValidator(DaoManager.getUserExistsDao(),
                DateCheck.getInstance(), PasswordCheck.getInstance(), PhoneNumberCheck.getInstance(),
                NameCheck.getInstance());
        updateUserValidator = new UpdateUserValidator(DaoManager.getUserExistsDao(),
                DateCheck.getInstance(), PhoneNumberCheck.getInstance(),
                NameCheck.getInstance(), PasswordCheck.getInstance());
    }
}
