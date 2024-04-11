package ru.arsentiev.validator.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.singleton.check.DateCheck;
import ru.arsentiev.singleton.check.NameCheck;
import ru.arsentiev.singleton.check.PasswordCheck;
import ru.arsentiev.singleton.check.PhoneNumberCheck;
import ru.arsentiev.repository.manager.DaoManager;
import ru.arsentiev.validator.RegistrationUserValidator;

@UtilityClass
public class ValidationManager {
    @Getter
    private static final RegistrationUserValidator registrationUserValidator;

    static {
        registrationUserValidator = new RegistrationUserValidator(DaoManager.getUserExistsDao(),
                DateCheck.getInstance(), PasswordCheck.getInstance(), PhoneNumberCheck.getInstance(),
                NameCheck.getInstance());
    }
}
