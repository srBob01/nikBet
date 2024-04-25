package ru.arsentiev.processing.check;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class NameCheck {
    private final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z]{1,40}");

    public boolean isIncorrect(String string) {
        return !NAME_PATTERN.matcher(string).matches();
    }

}
