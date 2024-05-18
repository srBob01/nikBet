package ru.arsentiev.processing.check;

import ru.arsentiev.processing.dateformatter.LocalDateFormatter;

import java.time.format.DateTimeParseException;
import java.util.Optional;

public class DateCheck implements Check {
    private final LocalDateFormatter localDateFormatter;

    public DateCheck(LocalDateFormatter localDateFormatter) {
        this.localDateFormatter = localDateFormatter;
    }

    @Override
    public boolean isInvalid(String str) {
        try {
            return Optional.of(str)
                    .map(localDateFormatter::format)
                    .isEmpty();
        } catch (DateTimeParseException e) {
            return true;
        }
    }
}
