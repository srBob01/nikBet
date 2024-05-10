package ru.arsentiev.servicelayer.validator.entity.login;

public enum LoginError {
    USER_NOT_FOUND("User with this login does not exist"),
    INCORRECT_PASSWORD("Incorrect password");
    private final String description;

    LoginError(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
