package ru.arsentiev.utils.check;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class PasswordCheck {
    private final Pattern PASSWORD_PATTEN = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}");

    public boolean isValid(String string) {
        return PASSWORD_PATTEN.matcher(string).matches();
    }
}
