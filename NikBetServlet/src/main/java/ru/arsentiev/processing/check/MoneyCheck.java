package ru.arsentiev.processing.check;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class MoneyCheck {
    private final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");

    public boolean isCorrect(String string) {
        return NUMBER_PATTERN.matcher(string).matches();
    }
}
