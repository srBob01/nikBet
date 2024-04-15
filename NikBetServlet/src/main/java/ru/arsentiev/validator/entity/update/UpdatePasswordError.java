package ru.arsentiev.validator.entity.update;

public enum UpdatePasswordError {
    PASSWORDS_DONT_MATCH("The entered password does not match your password"),
    INCORRECT_NEW_PASSWORD("The new password is incorrect");
    private final String description;

    UpdatePasswordError(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
