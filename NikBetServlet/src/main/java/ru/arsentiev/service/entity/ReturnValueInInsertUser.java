package ru.arsentiev.service.entity;

import ru.arsentiev.validator.entity.MyError;

import java.util.List;

public record ReturnValueInInsertUser(List<MyError> myErrors, Long idInsertUser) {
}
