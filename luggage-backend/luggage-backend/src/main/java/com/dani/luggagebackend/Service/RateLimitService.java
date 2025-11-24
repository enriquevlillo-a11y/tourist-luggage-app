package com.dani.luggagebackend.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing rate limiting using Bucket4j.
 * Implements token bucket algorithm to prevent brute-force attacks.
 */
@Service
public class RateLimitService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * Creates a bucket for login/register endpoints.
     * Allows 5 requests per minute per IP.
     */
    public Bucket resolveAuthBucket(String key) {
        return cache.computeIfAbsent(key, k -> createAuthBucket());
    }

    /**
     * Creates a bucket for general API endpoints.
     * Allows 100 requests per minute per IP.
     */
    public Bucket resolveGeneralBucket(String key) {
        return cache.computeIfAbsent(key, k -> createGeneralBucket());
    }

    private Bucket createAuthBucket() {
        // 5 requests per minute
        Bandwidth limit = Bandwidth.simple(5, Duration.ofMinutes(1));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket createGeneralBucket() {
        // 100 requests per minute
        Bandwidth limit = Bandwidth.simple(100, Duration.ofMinutes(1));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
