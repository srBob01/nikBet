package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.processing.check.*;
import ru.arsentiev.processing.dateformatter.LocalDateFormatter;

@UtilityClass
public class CheckManager {
    private static final LocalDateFormatter localDateFormatter;
    @Getter
    private static final DateCheck dateCheck;
    @Getter
    private static final NameCheck nameCheck;
    @Getter
    private static final PasswordCheck passwordCheck;
    @Getter
    private static final PhoneNumberCheck phoneNumberCheck;

    static {
        localDateFormatter = DateFormatterManager.getLocalDateFormatter();
        dateCheck = new DateCheck(localDateFormatter);
        nameCheck = new NameCheck();
        passwordCheck = new PasswordCheck();
        phoneNumberCheck = new PhoneNumberCheck();
    }
}
