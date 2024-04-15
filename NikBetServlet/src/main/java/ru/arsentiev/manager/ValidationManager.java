package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.singleton.check.*;
import ru.arsentiev.validator.MoneyValidator;
import ru.arsentiev.validator.RegistrationUserValidator;
import ru.arsentiev.validator.UpdateUserValidator;

@UtilityClass
public class ValidationManager {
    @Getter
    private static final RegistrationUserValidator registrationUserValidator;
    @Getter
    private static final UpdateUserValidator updateUserValidator;
    @Getter
    private static final MoneyValidator moneyValidator;

    static {
        registrationUserValidator = new RegistrationUserValidator(DaoManager.getUserExistsDao(),
                DateCheck.getInstance(), PasswordCheck.getInstance(), PhoneNumberCheck.getInstance(),
                NameCheck.getInstance());
        updateUserValidator = new UpdateUserValidator(DaoManager.getUserExistsDao(),
                DateCheck.getInstance(), PhoneNumberCheck.getInstance(),
                NameCheck.getInstance(), PasswordCheck.getInstance());
        moneyValidator = new MoneyValidator(MoneyCheck.getInstance());
    }
}
