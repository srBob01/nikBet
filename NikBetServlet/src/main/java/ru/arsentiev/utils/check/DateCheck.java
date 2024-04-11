package ru.arsentiev.utils.check;

import lombok.experimental.UtilityClass;
import ru.arsentiev.utils.LocalDateFormatter;

import java.time.format.DateTimeParseException;
import java.util.Optional;

@UtilityClass
public class DateCheck {
    public boolean isValid(String date) {
        try {
            return Optional.ofNullable(date)
                    .map(LocalDateFormatter::format)
                    .isPresent();
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
