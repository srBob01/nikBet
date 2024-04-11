package ru.arsentiev.validator.entity;

import lombok.Value;

@Value(staticConstructor = "of")
public class MyError {
    String field;
    TypeError type;
}
