package ru.arsentiev.processing.check;

public class EmptyCheck implements Check {
    @Override
    public boolean isInvalid(String str) {
        return false;
    }
}
