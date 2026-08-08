package com.pamir.ppfarmsbackend.shared.security.ratelimit;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * Maximum allowed requests in the time window.
     */
    int limit() default 60;

    /**
     * Time window in seconds.
     */
    int windowSeconds() default 60;

    /**
     * Rate limit key strategy (IP, USER, or TENANT).
     */
    RateLimitKeyType keyType() default RateLimitKeyType.IP;
}
