package ru.arsentiev.singleton.check;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)

public class NameCheck {
    private static final NameCheck INSTANCE = new NameCheck();

    public static NameCheck getInstance() {
        return INSTANCE;
    }

    private final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z]{1,40}");

    public boolean isIncorrect(String string) {
        return !NAME_PATTERN.matcher(string).matches();
    }

}
