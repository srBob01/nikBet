package ru.arsentiev.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class TimeStampFormatter {
    private final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    public final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    public LocalDateTime format(String dateTime) {
        return LocalDateTime.parse(dateTime, FORMATTER);
    }
}
