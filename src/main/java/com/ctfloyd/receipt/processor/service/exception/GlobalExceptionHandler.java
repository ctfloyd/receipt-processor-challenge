package com.ctfloyd.receipt.processor.service.exception;

import com.ctfloyd.receipt.processor.service.metrics.IMetrics;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

/**
 * Handles all exceptions thrown by the service. All exceptions return an empty response body with a HTTP status code
 * corresponding to the type of error that occurred.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String METRICS_NAMESPACE = "ServiceErrors";

    private final IMetrics metrics;

    @Autowired
    public GlobalExceptionHandler(IMetrics metrics) {
        this.metrics = Objects.requireNonNull(metrics, "Metrics is required.");
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<?> handleServiceException(ServiceException serviceException, WebRequest webRequest) {
        ErrorCode errorCode = serviceException.getErrorCode();
        metrics.increment(METRICS_NAMESPACE, "Error.Count.Http." + errorCode.getHttpCode());
        metrics.increment(METRICS_NAMESPACE, "Error.Count.Code." + errorCode.name());
        return new ResponseEntity<>(HttpStatusCode.valueOf(errorCode.getHttpCode()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException exception, WebRequest request) {
        metrics.increment(METRICS_NAMESPACE, "Invalid.Endpoint.Called");
        LOGGER.warn("A call was made to an unrecognized endpoint ({}).", getUri(request));
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUncaughtException(Exception exception, WebRequest webRequest) {
        metrics.increment(METRICS_NAMESPACE, "Error.Count.Http.500");
        metrics.increment(METRICS_NAMESPACE, "Error.Count.Uncaught");
        LOGGER.error("AN UNCAUGHT EXCEPTION OCCURRED!", exception);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(JsonMappingException exception, WebRequest request) {
        metrics.increment(METRICS_NAMESPACE, "Error.Count.Http.400");
        metrics.increment(METRICS_NAMESPACE, "Error.Count.HttpMessageNotReadable");
        LOGGER.warn("Could not map given JSON object to a java object.", exception);
        // Assume it's the caller's fault.
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private String getUri(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) request;
            return servletWebRequest.getRequest().getRequestURI();
        } else {
            return "UNKNOWN";
        }
    }

}
