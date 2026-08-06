package dev.salt.Ring20.exception;

public class QuotaExceededException extends RuntimeException {
    public QuotaExceededException(String message) {
        super(message);
    }

    public QuotaExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
