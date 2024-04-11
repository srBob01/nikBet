package ru.arsentiev.validator;

import ru.arsentiev.validator.entity.ValidationResult;

public interface Validator<T> {
    ValidationResult isValid(T obj);
}
