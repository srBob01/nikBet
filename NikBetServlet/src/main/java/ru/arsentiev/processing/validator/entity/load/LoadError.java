package ru.arsentiev.processing.validator.entity.load;

import lombok.Value;

@Value(staticConstructor = "of")
public class LoadError {
    String field;
    TypeLoadError type;
}
