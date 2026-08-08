package com.pamir.ppfarmsbackend.shared.security.ratelimit;

import com.pamir.ppfarmsbackend.shared.exception.RateLimitException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimiterAspect {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterAspect.class);

    private final StringRedisTemplate redisTemplate;

    public RateLimiterAspect(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(rateLimit)")
    public Object applyRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        String clientKey = resolveClientKey(request, rateLimit.keyType());
        String redisKey = "rate_limit:" + rateLimit.keyType() + ":" + clientKey;

        try {
            Long currentRequests = redisTemplate.opsForValue().increment(redisKey);

            if (currentRequests != null && currentRequests == 1) {
                redisTemplate.expire(redisKey, rateLimit.windowSeconds(), TimeUnit.SECONDS);
            }

            Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            long resetSeconds = (ttl != null && ttl > 0) ? ttl : rateLimit.windowSeconds();

            long remaining = Math.max(0, rateLimit.limit() - (currentRequests != null ? currentRequests : 0));

            if (response != null) {
                response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.limit()));
                response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
                response.setHeader("X-RateLimit-Reset", String.valueOf(resetSeconds));
            }

            if (currentRequests != null && currentRequests > rateLimit.limit()) {
                log.warn("RATE LIMIT EXCEEDED for Key [{}] on endpoint [{}]. Request Count: {}/{}",
                        redisKey, request.getRequestURI(), currentRequests, rateLimit.limit());
                throw new RateLimitException("API Rate Limit Exceeded. Maximum " + rateLimit.limit() +
                        " requests per " + rateLimit.windowSeconds() + " seconds.", rateLimit.limit(), resetSeconds);
            }
        } catch (RateLimitException rle) {
            throw rle;
        } catch (Exception e) {
            log.warn("Redis unavailable for RateLimiterAspect. Bypassing rate limit check. Error: {}", e.getMessage());
        }

        return joinPoint.proceed();
    }

    private String resolveClientKey(HttpServletRequest request, RateLimitKeyType keyType) {
        switch (keyType) {
            case USER:
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                    return auth.getName();
                }
                return getClientIp(request);

            case TENANT:
                String path = request.getRequestURI();
                if (path.contains("/organizations/")) {
                    String[] parts = path.split("/organizations/");
                    if (parts.length > 1) {
                        return parts[1].split("/")[0];
                    }
                }
                return getClientIp(request);

            case IP:
            default:
                return getClientIp(request);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
