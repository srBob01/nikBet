package ru.arsentiev.exception;

public class DaoException extends RuntimeException {
    public DaoException(Throwable e) {
        super(e);
    }
}
