package com.coopcredit.creditapplication.infrastructure.exception;

import com.coopcredit.creditapplication.infrastructure.metrics.MetricsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler using RFC 7807 Problem Details.
 * Provides standardized error responses across the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private final MetricsService metricsService;
    
    public GlobalExceptionHandler(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String TIMESTAMP = "timestamp";
    private static final String ERRORS = "errors";

    /**
     * Handles validation errors from @Valid annotations.
     *
     * @param ex validation exception
     * @param request HTTP request
     * @return problem detail with validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        metricsService.incrementValidationError();
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Validation failed for one or more fields"
        );
        
        problemDetail.setType(URI.create("https://coopcredit.com/errors/validation-error"));
        problemDetail.setTitle("Validation Error");
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty(ERRORS, errors);
        problemDetail.setProperty("path", request.getRequestURI());

        logger.warn("Validation error on {}: {}", request.getRequestURI(), errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Handles business rule violations.
     *
     * @param ex business exception
     * @param request HTTP request
     * @return problem detail
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {
        
        metricsService.incrementBusinessError();
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            ex.getMessage()
        );
        
        problemDetail.setType(URI.create("https://coopcredit.com/errors/business-rule-violation"));
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty("code", ex.getCode());
        problemDetail.setProperty("path", request.getRequestURI());

        logger.warn("Business exception on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }

    /**
     * Handles resource not found errors.
     *
     * @param ex resource not found exception
     * @param request HTTP request
     * @return problem detail
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
        
        problemDetail.setType(URI.create("https://coopcredit.com/errors/resource-not-found"));
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty("resourceName", ex.getResourceName());
        problemDetail.setProperty("fieldName", ex.getFieldName());
        problemDetail.setProperty("fieldValue", ex.getFieldValue());
        problemDetail.setProperty("path", request.getRequestURI());

        logger.warn("Resource not found on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    /**
     * Handles authentication errors.
     *
     * @param ex authentication exception
     * @param request HTTP request
     * @return problem detail
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ProblemDetail> handleAuthenticationException(
            Exception ex,
            HttpServletRequest request) {
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Authentication failed"
        );
        
        problemDetail.setType(URI.create("https://coopcredit.com/errors/authentication-failed"));
        problemDetail.setTitle("Authentication Failed");
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty("path", request.getRequestURI());

        logger.warn("Authentication failed on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    /**
     * Handles access denied errors (authorization).
     *
     * @param ex access denied exception
     * @param request HTTP request
     * @return problem detail
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN,
            "Access denied"
        );
        
        problemDetail.setType(URI.create("https://coopcredit.com/errors/access-denied"));
        problemDetail.setTitle("Access Denied");
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty("path", request.getRequestURI());

        logger.warn("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

    /**
     * Handles JWT specific errors.
     *
     * @param ex JWT exception
     * @param request HTTP request
     * @return problem detail
     */
    @ExceptionHandler({ExpiredJwtException.class, MalformedJwtException.class, SignatureException.class})
    public ResponseEntity<ProblemDetail> handleJwtException(
            Exception ex,
            HttpServletRequest request) {
        
        String detail = "Invalid or expired token";
        if (ex instanceof ExpiredJwtException) {
            detail = "Token has expired";
        } else if (ex instanceof MalformedJwtException) {
            detail = "Malformed token";
        } else if (ex instanceof SignatureException) {
            detail = "Invalid token signature";
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            detail
        );
        
        problemDetail.setType(URI.create("https://coopcredit.com/errors/invalid-token"));
        problemDetail.setTitle("Invalid Token");
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty("path", request.getRequestURI());

        logger.warn("JWT error on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    /**
     * Handles illegal argument exceptions (typically from validation).
     *
     * @param ex illegal argument exception
     * @param request HTTP request
     * @return problem detail
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
        
        problemDetail.setType(URI.create("https://coopcredit.com/errors/invalid-argument"));
        problemDetail.setTitle("Invalid Argument");
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty("path", request.getRequestURI());

        logger.warn("Invalid argument on {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Handles all other unexpected exceptions.
     *
     * @param ex exception
     * @param request HTTP request
     * @return problem detail
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
        );
        
        problemDetail.setType(URI.create("https://coopcredit.com/errors/internal-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty("path", request.getRequestURI());

        logger.error("Unexpected error on {}: ", request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
