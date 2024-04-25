package ru.arsentiev.processing.check;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PhoneNumberCheck {
    private final Pattern PHONE_PATTERN = Pattern.compile("^\\+7\\d{10}$");

    public boolean isIncorrect(String string) {
        return !PHONE_PATTERN.matcher(string).matches();
    }
}
