package ru.arsentiev.validator.entity;

public enum TypeError {
    NON_UNIQUE("Non-unique value"),
    EMPTY("Empty value"),
    INCORRECT("Incorrect value"),
    YOUNG_USER("User too young");

    private final String description;

    TypeError(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
