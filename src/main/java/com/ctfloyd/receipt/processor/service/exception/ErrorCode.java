package com.ctfloyd.receipt.processor.service.exception;

/**
 * Defines a type of error that can occur in the service and its corresponding http status code.
 */
public enum ErrorCode {
    INVALID_INPUT(400),
    NOT_FOUND(404),
    GENERIC_ERROR(500);

    private final int httpCode;

    ErrorCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

}
