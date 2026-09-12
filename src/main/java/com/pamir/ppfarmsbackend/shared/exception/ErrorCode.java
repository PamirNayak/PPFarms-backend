package com.pamir.ppfarmsbackend.shared.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Standard machine-readable Error Codes conforming to RFC 7807/9457 enterprise standard.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Identity & Multi-Tenancy
    UNAUTHORIZED_ACCESS("AUTH_001", HttpStatus.UNAUTHORIZED, "Authentication required"),
    BAD_CREDENTIALS("AUTH_002", HttpStatus.UNAUTHORIZED, "Invalid email or password"),
    SESSION_EXPIRED("AUTH_003", HttpStatus.UNAUTHORIZED, "Session has expired or is invalid"),
    ORGANIZATION_NOT_FOUND("TENANT_001", HttpStatus.UNAUTHORIZED, "Farm organization not found or deactivated"),
    TENANT_ACCESS_DENIED("TENANT_002", HttpStatus.FORBIDDEN, "Access to another farm's data is strictly prohibited"),
    ACCESS_DENIED("AUTH_004", HttpStatus.FORBIDDEN, "Insufficient permissions for this operation"),

    // Billing, Plans & Entitlements
    PLAN_QUOTA_EXCEEDED("BILLING_001", HttpStatus.PAYMENT_REQUIRED, "Plan animal count limit reached"),
    SUBSCRIPTION_EXPIRED("BILLING_002", HttpStatus.PAYMENT_REQUIRED, "Subscription has expired. Account is in read-only mode"),
    SPECIES_NOT_ENTITLED("BILLING_003", HttpStatus.FORBIDDEN, "Active plan does not support this livestock species"),
    PAYMENT_REQUIRED("BILLING_004", HttpStatus.PAYMENT_REQUIRED, "Valid subscription required"),
    PAYMENT_FAILED("BILLING_005", HttpStatus.BAD_REQUEST, "Payment processing failed"),

    // Data & Resource Management
    RESOURCE_NOT_FOUND("DATA_001", HttpStatus.NOT_FOUND, "Requested record was not found"),
    DUPLICATE_RESOURCE("DATA_002", HttpStatus.CONFLICT, "Record with identical identifier already exists"),
    RESOURCE_HAS_DEPENDENTS("DATA_003", HttpStatus.CONFLICT, "Cannot delete record because active dependent records exist"),
    INVALID_OPERATION("DATA_004", HttpStatus.BAD_REQUEST, "Invalid business operation requested"),
    BUSINESS_RULE_VIOLATION("DATA_005", HttpStatus.UNPROCESSABLE_ENTITY, "Business validation rule failed"),

    // Feed & Production
    INSUFFICIENT_STOCK("FEED_001", HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient feed inventory available"),

    // Request & Protocol
    VALIDATION_FAILED("REQ_001", HttpStatus.BAD_REQUEST, "Request validation failed"),
    MALFORMED_JSON("REQ_002", HttpStatus.BAD_REQUEST, "Malformed JSON request body"),
    TYPE_MISMATCH("REQ_003", HttpStatus.BAD_REQUEST, "Request parameter type mismatch"),
    METHOD_NOT_ALLOWED("REQ_004", HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not supported"),
    MEDIA_TYPE_NOT_SUPPORTED("REQ_005", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type"),
    FILE_TOO_LARGE("REQ_006", HttpStatus.PAYLOAD_TOO_LARGE, "Uploaded file exceeds maximum allowed size"),

    // System & Resilience
    RATE_LIMIT_EXCEEDED("SYS_001", HttpStatus.TOO_MANY_REQUESTS, "API rate limit exceeded"),
    DATABASE_UNAVAILABLE("SYS_002", HttpStatus.SERVICE_UNAVAILABLE, "Database service is temporarily unavailable"),
    QUERY_TIMEOUT("SYS_003", HttpStatus.GATEWAY_TIMEOUT, "Database query timed out"),
    INTERNAL_SERVER_ERROR("SYS_999", HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred");

    private final String code;
    private final HttpStatus defaultStatus;
    private final String defaultMessage;
}
