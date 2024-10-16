package com.ctfloyd.receipt.processor.service.exception;

/**
 * An exception that is thrown by the service to indicate an error condition has been handled. The exception will be
 * caught and handled by the global exception handler.
 */
public class ServiceException extends RuntimeException {

    private final ErrorCode errorCode;

    public ServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
