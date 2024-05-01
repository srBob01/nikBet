package ru.arsentiev.exception;

public class ConnectionException extends RuntimeException {
    public ConnectionException(String str, Exception e) {
        super(str, e);
    }
}
