package ru.arsentiev.exception;

public class ServiceException extends RuntimeException {
    public ServiceException(String e) {
        super(e);
    }
}
