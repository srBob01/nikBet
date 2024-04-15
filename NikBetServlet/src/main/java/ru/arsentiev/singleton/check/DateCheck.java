package ru.arsentiev.singleton.check;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.utils.LocalDateFormatter;

import java.time.format.DateTimeParseException;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateCheck {
    private static final DateCheck INSTANCE = new DateCheck();

    public static DateCheck getInstance() {
        return INSTANCE;
    }

    public boolean isCorrect(String date) {
        try {
            return Optional.of(date)
                    .map(LocalDateFormatter::format)
                    .isPresent();
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
