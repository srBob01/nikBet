package ru.arsentiev.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@UtilityClass
public class LocalDateFormatter {
    private static final String PATTERN = "dd-MM -yyyy";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    public LocalDate format(String date) {
        return LocalDate.parse(date, FORMATTER);
    }
}