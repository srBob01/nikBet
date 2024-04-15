package ru.arsentiev.singleton.check;

import java.util.regex.Pattern;

public class MoneyCheck {
    private static final MoneyCheck INSTANCE = new MoneyCheck();

    public static MoneyCheck getInstance() {
        return INSTANCE;
    }

    private final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");

    public boolean isCorrect(String string) {
        return NUMBER_PATTERN.matcher(string).matches();
    }
}
