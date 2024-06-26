package ru.arsentiev.processing.dateformatter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class TimeStampFormatter {
    private final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final String ISO_PATTERN = "yyyy-MM-dd'T'HH:mm";
    public final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);
    public final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern(ISO_PATTERN);

    public LocalDateTime format(String dateTime) {
        return LocalDateTime.parse(dateTime, FORMATTER);
    }

    public LocalDateTime formatISO(String dateTime) {
        return LocalDateTime.parse(dateTime, ISO_FORMATTER);
    }
}
