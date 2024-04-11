package ru.arsentiev.utils.check;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class PhoneNumberCheck {
    private final Pattern PHONE_PATTERN = Pattern.compile("^\\+7\\d{10}$");

    public boolean isValid(String string) {
        return PHONE_PATTERN.matcher(string).matches();
    }
}
