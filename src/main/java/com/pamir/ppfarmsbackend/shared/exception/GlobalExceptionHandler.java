package com.pamir.ppfarmsbackend.shared.exception;

import com.pamir.ppfarmsbackend.shared.domain.ApiResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================================
    // 1. CUSTOM MULTI-TENANT SAAS DOMAIN EXCEPTIONS
    // =========================================================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("[404 NOT FOUND] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        log.warn("[400 BAD REQUEST] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.INVALID_OPERATION.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        log.warn("[401 UNAUTHORIZED] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.UNAUTHORIZED_ACCESS.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(TenantAccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleTenantAccessDenied(TenantAccessDeniedException ex, HttpServletRequest request) {
        log.warn("[403 TENANT VIOLATION] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Tenant Access Violation: " + ex.getMessage(), ErrorCode.TENANT_ACCESS_DENIED.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        log.warn("[409 CONFLICT] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.DUPLICATE_RESOURCE.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRuleViolation(BusinessRuleViolationException ex, HttpServletRequest request) {
        log.warn("[422 BUSINESS RULE] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.BUSINESS_RULE_VIOLATION.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        log.warn("[422 INSUFFICIENT STOCK] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.INSUFFICIENT_STOCK.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(PlanQuotaExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleQuotaExceeded(PlanQuotaExceededException ex, HttpServletRequest request) {
        log.warn("[402 QUOTA EXCEEDED] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.PLAN_QUOTA_EXCEEDED.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(SubscriptionExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleSubscriptionExpired(SubscriptionExpiredException ex, HttpServletRequest request) {
        log.warn("[402 SUBSCRIPTION EXPIRED] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.SUBSCRIPTION_EXPIRED.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidOperation(InvalidOperationException ex, HttpServletRequest request) {
        log.warn("[400 INVALID OPERATION] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.INVALID_OPERATION.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ApiResponse<Void>> handleRateLimit(RateLimitException ex, HttpServletRequest request) {
        log.warn("[429 RATE LIMIT] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.RATE_LIMIT_EXCEEDED.getCode(), request.getRequestURI()));
    }

    // =========================================================================
    // 2. SECURITY & AUTHENTICATION EXCEPTIONS
    // =========================================================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("[401 BAD CREDENTIALS] Invalid login attempt on {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid email or password", ErrorCode.BAD_CREDENTIALS.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler({InsufficientAuthenticationException.class, AuthenticationException.class})
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        log.warn("[401 AUTH REQUIRED] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Authentication required to access this resource.", ErrorCode.UNAUTHORIZED_ACCESS.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException ex, HttpServletRequest request) {
        log.warn("[401 JWT ERROR] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Session token is invalid or expired. Please sign in again.", ErrorCode.SESSION_EXPIRED.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("[403 ACCESS DENIED] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Access denied: You do not have permission to perform this action.", ErrorCode.ACCESS_DENIED.getCode(), request.getRequestURI()));
    }

    // =========================================================================
    // 3. DATABASE, JPA & PERSISTENCE CONSTRAINTS (SANITIZED & TRANSLATED)
    // =========================================================================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("[409 DATA INTEGRITY ERROR] Path: {} | Exception: {}", request.getRequestURI(), ex.getMessage());

        String detailedMessage = ex.getMostSpecificCause().getMessage();
        String userFriendlyMessage = "Database constraint violation. Please verify entered data.";

        if (detailedMessage != null) {
            if (detailedMessage.contains("subscriptions_organization_id_fkey") || detailedMessage.contains("violates foreign key constraint \"subscriptions_organization_id_fkey\"")) {
                userFriendlyMessage = "Your farm organization was not found or has been deactivated. Please sign in again.";
            } else if (detailedMessage.contains("animals_organization_id_fkey")) {
                userFriendlyMessage = "Farm organization profile was not found.";
            } else if (detailedMessage.contains("sales_customer_id_fkey")) {
                userFriendlyMessage = "Cannot delete or alter this customer because existing sales records or invoices are linked to them.";
            } else if (detailedMessage.contains("purchases_supplier_id_fkey")) {
                userFriendlyMessage = "Cannot delete or alter this supplier because existing purchase orders are linked to them.";
            } else if (detailedMessage.contains("tag_number") || detailedMessage.contains("animals_tag_number_key")) {
                userFriendlyMessage = "An animal with this Ear Tag Number is already registered in your farm.";
            } else if (detailedMessage.contains("species_name_key")) {
                userFriendlyMessage = "A livestock species with this name already exists in the system registry.";
            } else if (detailedMessage.contains("sheds_pens_org_name_unique")) {
                userFriendlyMessage = "A shed/pen location with this name is already registered on your farm.";
            } else if (detailedMessage.contains("users_email_key")) {
                userFriendlyMessage = "An account with this email address is already registered.";
            }
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(userFriendlyMessage, ErrorCode.RESOURCE_HAS_DEPENDENTS.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler({ConcurrencyFailureException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<ApiResponse<Void>> handleConcurrencyFailure(Exception ex, HttpServletRequest request) {
        log.warn("[409 CONCURRENCY CONFLICT] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Record was modified concurrently by another user. Please refresh and retry.", ErrorCode.INVALID_OPERATION.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(CannotGetJdbcConnectionException.class)
    public ResponseEntity<ApiResponse<Void>> handleConnectionException(CannotGetJdbcConnectionException ex, HttpServletRequest request) {
        log.error("[503 DB POOL UNAVAILABLE] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error("Database service is temporarily unavailable. Please retry in a few moments.", ErrorCode.DATABASE_UNAVAILABLE.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(QueryTimeoutException.class)
    public ResponseEntity<ApiResponse<Void>> handleQueryTimeout(QueryTimeoutException ex, HttpServletRequest request) {
        log.error("[504 QUERY TIMEOUT] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(ApiResponse.error("Database query timed out. Please refine your search criteria or retry.", ErrorCode.QUERY_TIMEOUT.getCode(), request.getRequestURI()));
    }

    // =========================================================================
    // 4. REQUEST VALIDATION, PAYLOAD & HTTP MAPPING EXCEPTIONS
    // =========================================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("[400 DTO VALIDATION FAILED] Path: {} | {} error(s)", request.getRequestURI(), errors.size());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed: Please check the highlighted fields.", ErrorCode.VALIDATION_FAILED.getCode(), request.getRequestURI(), errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));
        log.warn("[400 CONSTRAINT VIOLATION] Path: {} | Errors: {}", request.getRequestURI(), errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation constraints violated.", ErrorCode.VALIDATION_FAILED.getCode(), request.getRequestURI(), errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("[400 MALFORMED JSON] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Malformed JSON request body or unparseable field format.", ErrorCode.MALFORMED_JSON.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("[400 TYPE MISMATCH] Path: {} | Field: {}, Required: {}", request.getRequestURI(), ex.getName(), ex.getRequiredType());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Parameter type mismatch for field '" + ex.getName() + "'. Expected type: " 
                        + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "valid value"), ErrorCode.TYPE_MISMATCH.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("[400 MISSING PARAM] Path: {} | Param: {}", request.getRequestURI(), ex.getParameterName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Missing required query parameter: '" + ex.getParameterName() + "'", ErrorCode.VALIDATION_FAILED.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingHeader(MissingRequestHeaderException ex, HttpServletRequest request) {
        log.warn("[400 MISSING HEADER] Path: {} | Header: {}", request.getRequestURI(), ex.getHeaderName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Missing required header: '" + ex.getHeaderName() + "'", ErrorCode.VALIDATION_FAILED.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("[404 ROUTE NOT FOUND] Path: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Requested API endpoint or resource was not found.", ErrorCode.RESOURCE_NOT_FOUND.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("[405 METHOD NOT ALLOWED] Path: {} | Method: {}", request.getRequestURI(), ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error("HTTP Method '" + ex.getMethod() + "' is not supported for this endpoint.", ErrorCode.METHOD_NOT_ALLOWED.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.warn("[415 MEDIA TYPE NOT SUPPORTED] Path: {} | ContentType: {}", request.getRequestURI(), ex.getContentType());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.error("Content type '" + ex.getContentType() + "' is not supported. Please use application/json.", ErrorCode.MEDIA_TYPE_NOT_SUPPORTED.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(ApiResponse.error("Requested media response representation is not acceptable.", ErrorCode.MEDIA_TYPE_NOT_SUPPORTED.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSize(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.warn("[413 FILE TOO LARGE] Path: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error("File upload size exceeds the maximum allowed server limit (5MB).", ErrorCode.FILE_TOO_LARGE.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseEntity<ApiResponse<Void>> handleAsyncTimeout(AsyncRequestTimeoutException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error("Async request processing timed out.", ErrorCode.QUERY_TIMEOUT.getCode(), request.getRequestURI()));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(RuntimeException ex, HttpServletRequest request) {
        log.warn("[400 ILLEGAL ARGUMENT] Path: {} | Error: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), ErrorCode.INVALID_OPERATION.getCode(), request.getRequestURI()));
    }

    // =========================================================================
    // 5. CATCH-ALL FALLBACK EXCEPTION HANDLER (ZERO INTERNAL LEAKAGE)
    // =========================================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("[500 INTERNAL SERVER ERROR] Path: {} | Unhandled exception: ", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected server error occurred. Please quote your trace ID if contacting support.", ErrorCode.INTERNAL_SERVER_ERROR.getCode(), request.getRequestURI()));
    }
}

