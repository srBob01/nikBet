package ru.arsentiev.processing.check;

import ru.arsentiev.processing.dateformatter.LocalDateFormatter;

import java.time.format.DateTimeParseException;
import java.util.Optional;

public class DateCheck {
    private final LocalDateFormatter localDateFormatter;

    public DateCheck(LocalDateFormatter localDateFormatter) {
        this.localDateFormatter = localDateFormatter;
    }

    public boolean isCorrect(String date) {
        try {
            return Optional.of(date)
                    .map(localDateFormatter::format)
                    .isPresent();
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
