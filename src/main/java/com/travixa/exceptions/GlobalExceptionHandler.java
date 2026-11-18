package com.travixa.exceptions;

import com.travixa.dto.common.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(
            UnauthorizedException exception, HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", exception.getMessage(), request);
    }

    @ExceptionHandler(PartnerDataIsolationException.class)
    public ResponseEntity<ApiErrorResponse> handlePartnerIsolation(
            PartnerDataIsolationException exception, HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN, "FORBIDDEN", exception.getMessage(), request);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(
            ForbiddenOperationException exception, HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN, "FORBIDDEN", exception.getMessage(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler({ DuplicateResourceException.class, ConflictException.class,
            DataIntegrityViolationException.class, BookingStateException.class, InvalidStateException.class })
    public ResponseEntity<ApiErrorResponse> handleConflict(
            Exception exception, HttpServletRequest request) {
        String message = exception instanceof DataIntegrityViolationException
                ? "The request conflicts with existing data"
                : exception.getMessage();
        return error(HttpStatus.CONFLICT, "CONFLICT", message, request);
    }

    @ExceptionHandler({ InvalidRequestException.class, ValidationException.class })
    public ResponseEntity<ApiErrorResponse> handleInvalidRequest(
            RuntimeException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", exception.getMessage(), request);
    }

    @ExceptionHandler({ ConstraintViolationException.class, HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class })
    public ResponseEntity<ApiErrorResponse> handleMalformedRequest(
            Exception exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "The request is invalid", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> validationErrors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage() == null
                                ? "Invalid value"
                                : fieldError.getDefaultMessage(),
                        (first, ignored) -> first,
                        TreeMap::new));
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Request validation failed", request, validationErrors);
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPayment(
            InvalidPaymentException exception, HttpServletRequest request) {
        return error(HttpStatus.UNPROCESSABLE_CONTENT, "INVALID_PAYMENT", exception.getMessage(), request);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiErrorResponse> handlePayment(
            PaymentException exception, HttpServletRequest request) {
        return error(HttpStatus.UNPROCESSABLE_CONTENT, "PAYMENT_ERROR", exception.getMessage(), request);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleExternalService(
            ExternalServiceException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_GATEWAY, "EXTERNAL_SERVICE_ERROR", "An upstream service failed", request);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiErrorResponse> handleFileStorage(
            FileStorageException exception, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_STORAGE_ERROR", "File storage operation failed", request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(
            ResponseStatusException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String message = exception.getReason() == null ? status.getReasonPhrase() : exception.getReason();
        return error(status, status.name(), message, request);
    }

    private ResponseEntity<ApiErrorResponse> error(
            HttpStatus status, String error, String message, HttpServletRequest request) {
        return error(status, error, message, request, Collections.emptyMap());
    }

    private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String error, String message,
            HttpServletRequest request, Map<String, String> validationErrors) {
        ApiErrorResponse body = new ApiErrorResponse(Instant.now(), status.value(), error,
                message == null || message.isBlank() ? status.getReasonPhrase() : message,
                request.getRequestURI(), validationErrors);
        return ResponseEntity.status(status).body(body);
    }
}
