package com.agrowmart.exception;

import com.agrowmart.exception.AuthExceptions.AuthenticationFailedException;
import com.agrowmart.exception.AuthExceptions.BusinessValidationException;
import com.agrowmart.exception.AuthExceptions.CloudinaryOperationException;
import com.agrowmart.exception.AuthExceptions.DuplicateResourceException;
import com.agrowmart.exception.AuthExceptions.FileUploadException;
import com.agrowmart.exception.AuthExceptions.InvalidOtpException;
import com.agrowmart.exception.ForbiddenException;
import com.agrowmart.exception.ResourceNotFoundException;
import com.agrowmart.exception.SubscriptionLimitExceededException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler for clean, consistent JSON error responses
 * Required for production: no stack traces to client, clear messages for frontend
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Catch ALL unexpected errors (fallback) – log full stack trace
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred", ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred. Please try again later.");
        body.put("path", request.getDescription(false));

        // In production: do NOT expose stack trace to client
        // Only log it (already done above)

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 2. Resource Not Found (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // 3. Forbidden / Access Denied (403)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbidden(ForbiddenException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    // 4. Subscription / Plan Limit Exceeded (403)
    @ExceptionHandler(SubscriptionLimitExceededException.class)
    public ResponseEntity<Object> handleSubscriptionLimit(SubscriptionLimitExceededException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Subscription Limit Exceeded");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    // 5. Validation Errors (@Valid DTOs) – 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        body.put("errors", errors);
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 6. Bean Validation on Path Variables / Request Params (e.g. @Min on @PathVariable)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Constraint Violation");

        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        body.put("errors", errors);
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 7. Wrong type in path variable (e.g. /products/abc instead of number)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Invalid Parameter");
        body.put("message", "Invalid value for parameter '" + ex.getName() + "': " + ex.getValue());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 8. File upload too large (multipart max size exceeded)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.PAYLOAD_TOO_LARGE.value());
        body.put("error", "Payload Too Large");
        body.put("message", "File size exceeds maximum allowed limit");
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.PAYLOAD_TOO_LARGE);
    }
    
 // ──────────────────────────────────────────────
//  Custom business & auth exceptions – 400 / 409 / 401 family
// ──────────────────────────────────────────────

// 409 Conflict – duplicate email/phone
@ExceptionHandler(DuplicateResourceException.class)
public ResponseEntity<Object> handleDuplicateResource(
        DuplicateResourceException ex,
        WebRequest request) {

    log.warn("Duplicate resource attempt: {}", ex.getMessage());

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.CONFLICT.value());
    body.put("error", "Conflict");
    body.put("message", ex.getMessage());           // ← "This email is already registered."
    body.put("path", request.getDescription(false));

    return new ResponseEntity<>(body, HttpStatus.CONFLICT);
}

// 400 – business rule violation / missing required field / invalid format
@ExceptionHandler(BusinessValidationException.class)
public ResponseEntity<Object> handleBusinessValidation(
        BusinessValidationException ex,
        WebRequest request) {

    log.warn("Business validation failed: {}", ex.getMessage());

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Bad Request");
    body.put("message", ex.getMessage());
    body.put("path", request.getDescription(false));

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
}

// 401 Unauthorized – login / credential failures
@ExceptionHandler(AuthenticationFailedException.class)
public ResponseEntity<Object> handleAuthenticationFailed(
        AuthenticationFailedException ex,
        WebRequest request) {

    log.warn("Authentication failed: {}", ex.getMessage());

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.UNAUTHORIZED.value());
    body.put("error", "Unauthorized");
    body.put("message", ex.getMessage());           // "Invalid email/phone or password"
    body.put("path", request.getDescription(false));

    return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
}

// 400 – OTP problems
@ExceptionHandler(InvalidOtpException.class)
public ResponseEntity<Object> handleInvalidOtp(
        InvalidOtpException ex,
        WebRequest request) {

    log.warn("Invalid OTP attempt: {}", ex.getMessage());

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Invalid OTP");
    body.put("message", ex.getMessage());
    body.put("path", request.getDescription(false));

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
}

// 400 / 500 family – file upload business logic errors
@ExceptionHandler(FileUploadException.class)
public ResponseEntity<Object> handleFileUploadException(
        FileUploadException ex,
        WebRequest request) {

    log.error("File upload problem", ex);

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "File Upload Error");
    body.put("message", ex.getMessage());
    body.put("path", request.getDescription(false));

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
}
//──────────────────────────────────────────────
//Cloudinary / Photo upload specific errors
//──────────────────────────────────────────────

//500 – Cloudinary failure (upload/delete)
@ExceptionHandler(CloudinaryOperationException.class)
public ResponseEntity<Object> handleCloudinaryOperation(
     CloudinaryOperationException ex,
     WebRequest request) {
 log.error("Cloudinary operation failed", ex);
 Map<String, Object> body = new HashMap<>();
 body.put("timestamp", LocalDateTime.now());
 body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
 body.put("error", "Image Processing Error");
 body.put("message", "Failed to process image with Cloudinary: " + ex.getMessage());
 body.put("path", request.getDescription(false));
 return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
}

//General multipart/form-data parsing issues (wrong format, missing boundary, etc.)
@ExceptionHandler({org.springframework.web.multipart.MultipartException.class,
                org.springframework.http.converter.HttpMessageNotReadableException.class})
public ResponseEntity<Object> handleMultipartErrors(
     Exception ex,
     WebRequest request) {
 log.warn("Multipart request error", ex);
 Map<String, Object> body = new HashMap<>();
 body.put("timestamp", LocalDateTime.now());
 body.put("status", HttpStatus.BAD_REQUEST.value());
 body.put("error", "Invalid Form Data");
 body.put("message", "Invalid multipart/form-data request. Make sure you're sending form-data with correct fields.");
 body.put("path", request.getDescription(false));
 return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
}
    // Optional: Add more custom exceptions if you have them
}