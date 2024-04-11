package ru.arsentiev.validator.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationResult {
    private final List<MyError> myErrors = new ArrayList<>();
    public void add(MyError myError) {
        myErrors.add(myError);
    }

    public boolean isEmpty() {
        return myErrors.isEmpty();
    }

}
