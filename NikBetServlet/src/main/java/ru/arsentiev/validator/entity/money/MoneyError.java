package ru.arsentiev.validator.entity.money;

public enum MoneyError {
    INCORRECT_VALUE("Enter correct value"),
    INSUFFICIENT_FUNDS("Insufficient funds on the balance sheet");
    private final String description;

    MoneyError(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
