package com.pamir.ppfarmsbackend.shared.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that logs every incoming HTTP API request, the requesting user, HTTP method, response status code, and latency.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Skip CORS pre-flight OPTIONS requests and static assets to eliminate log noise
        if ("OPTIONS".equalsIgnoreCase(method) || uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".ico") || uri.endsWith(".png")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userIdentifier = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                    ? auth.getName() : "Anonymous";

            // Suppress expected 403s on unauthenticated initial check endpoints to prevent log clutter
            if (status == 403 && ("Anonymous".equals(userIdentifier) || auth == null)) {
                if (uri.contains("/users/me") || uri.contains("/organizations/me")) {
                    return;
                }
            }

            if (status >= 500) {
                log.error("[HTTP ERROR] {} {} -> Status: {} | User: {} | Latency: {}ms", method, uri, status, userIdentifier, duration);
            } else if (status >= 400) {
                log.warn("[HTTP WARN] {} {} -> Status: {} | User: {} | Latency: {}ms", method, uri, status, userIdentifier, duration);
            } else {
                log.info("[HTTP] {} {} -> Status: {} | User: {} | Latency: {}ms", method, uri, status, userIdentifier, duration);
            }
        }
    }
}
