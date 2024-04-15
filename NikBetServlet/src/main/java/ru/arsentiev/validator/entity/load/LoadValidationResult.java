package ru.arsentiev.validator.entity.load;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LoadValidationResult {
    private final List<LoadError> loadErrors = new ArrayList<>();
    public void add(LoadError loadError) {
        loadErrors.add(loadError);
    }

    public boolean isEmpty() {
        return loadErrors.isEmpty();
    }

}
