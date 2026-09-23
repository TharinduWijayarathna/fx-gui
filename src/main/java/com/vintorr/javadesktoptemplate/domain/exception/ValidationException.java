package com.vintorr.javadesktoptemplate.domain.exception;

/**
 * A rejected input, carrying the field it belongs to so the presentation layer can attach
 * the message to the right control — the desktop equivalent of Laravel's validator errors.
 */
public class ValidationException extends RuntimeException {

    private final String field;

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}
