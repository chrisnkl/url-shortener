package com.chrisnkl.shortenurl.infrastructure.cache;

import com.chrisnkl.shortenurl.domain.ports.out.CachePort;
import com.github.benmanes.caffeine.cache.Cache;
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
    private static final String REDIS_KEY = "url:";

    @Override
    public void put(String key, String value) {
        localCaffeineCache.put(key, value);
        redisTemplate.opsForValue().set("url:" + key, value, Duration.ofDays(7));
    }

    @Override
    public Optional<String> get(String key) {

        // Check Level 1 - Ram
        String localValue = localCaffeineCache.getIfPresent(key);
        if (localValue != null) return Optional.of(localValue);

        // Check Level 2 - Redis
        String redisValue = redisTemplate.opsForValue().get(REDIS_KEY + key);
        if (redisValue != null) {

            // Backfill level 1
            localCaffeineCache.put(key, redisValue);

            return Optional.of(redisValue);
        }

        return Optional.empty();

    }
}
