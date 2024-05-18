package ru.arsentiev.processing.check;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class NameCheck implements Check {
    private final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z]{1,40}");

    @Override
    public boolean isInvalid(String str) {
        return !NAME_PATTERN.matcher(str).matches();
    }
}
