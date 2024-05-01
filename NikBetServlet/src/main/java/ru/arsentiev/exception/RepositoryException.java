package ru.arsentiev.exception;

public class RepositoryException extends RuntimeException {
    public RepositoryException(Throwable e) {
        super(e);
    }
}
