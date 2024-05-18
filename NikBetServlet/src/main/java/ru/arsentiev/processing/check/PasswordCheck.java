package ru.arsentiev.processing.check;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PasswordCheck implements Check {
    private final Pattern PASSWORD_PATTEN = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)\\S[a-zA-Z\\d]{8,20}");

    @Override
    public boolean isInvalid(String str) {
        return !PASSWORD_PATTEN.matcher(str).matches();
    }
}
