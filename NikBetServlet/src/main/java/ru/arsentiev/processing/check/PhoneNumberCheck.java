package ru.arsentiev.processing.check;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PhoneNumberCheck implements Check {
    private final Pattern PHONE_PATTERN = Pattern.compile("^\\+7\\d{10}$");

    @Override
    public boolean isInvalid(String str) {
        return !PHONE_PATTERN.matcher(str).matches();
    }
}
