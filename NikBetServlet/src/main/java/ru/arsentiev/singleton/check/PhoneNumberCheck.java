package ru.arsentiev.singleton.check;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PhoneNumberCheck {
    private static final PhoneNumberCheck INSTANCE = new PhoneNumberCheck();

    public static PhoneNumberCheck getInstance() {
        return INSTANCE;
    }
    private final Pattern PHONE_PATTERN = Pattern.compile("^\\+7\\d{10}$");

    public boolean isIncorrect(String string) {
        return !PHONE_PATTERN.matcher(string).matches();
    }
}
