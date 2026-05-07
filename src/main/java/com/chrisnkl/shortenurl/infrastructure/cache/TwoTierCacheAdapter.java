package com.chrisnkl.shortenurl.infrastructure.cache;

import com.chrisnkl.shortenurl.domain.ports.out.CachePort;
import com.github.benmanes.caffeine.cache.Cache;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TwoTierCacheAdapter implements CachePort {

    private final Cache<String, String> localCaffeineCache;
    private final StringRedisTemplate redisTemplate;

    @CircuitBreaker(name = "redisCache", fallbackMethod = "fallbackPut")
    @Override
    public void put(String key, String value) {
        localCaffeineCache.put(key, value);
        redisTemplate.opsForValue().set(key, value, Duration.ofDays(7));
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "fallbackGet")
    @Override
    public Optional<String> get(String key) {

        // Check Level 1 - Ram
        String localValue = localCaffeineCache.getIfPresent(key);
        if (localValue != null) return Optional.of(localValue);

        // Check Level 2 - Redis
        String redisValue = redisTemplate.opsForValue().get(key);
        if (redisValue != null) {

            // Backfill level 1
            localCaffeineCache.put(key, redisValue);

            return Optional.of(redisValue);
        }

        return Optional.empty();

    }

    public void fallbackPut(String key, String value, Throwable t) {
        localCaffeineCache.put(key, value);
    }

    public Optional<String> fallbackGet(String key, Throwable t) {
        return Optional.ofNullable(localCaffeineCache.getIfPresent(key));
    }

}
