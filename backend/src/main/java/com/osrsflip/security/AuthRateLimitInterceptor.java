package com.osrsflip.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthRateLimitInterceptor implements HandlerInterceptor {

    // Separate in-memory bucket stores per IP address.
    // In a multi-instance deployment these should move to Redis.
    private final ConcurrentHashMap<String, Bucket> loginBuckets    = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> registerBuckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();
        String ip   = resolveClientIp(request);

        Bucket bucket = switch (path) {
            case "/api/auth/login"    -> loginBuckets.computeIfAbsent(ip, k -> loginBucket());
            case "/api/auth/register" -> registerBuckets.computeIfAbsent(ip, k -> registerBucket());
            default -> null;
        };

        if (bucket != null && !bucket.tryConsume(1)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Too many requests — please wait before trying again\"}");
            return false;
        }

        return true;
    }

    // 5 login attempts per minute per IP
    private Bucket loginBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
                .build();
    }

    // 3 register attempts per minute per IP
    private Bucket registerBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(3, Refill.greedy(3, Duration.ofMinutes(1))))
                .build();
    }

    // Respect X-Forwarded-For when behind a reverse proxy (nginx, load balancer).
    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }
}
