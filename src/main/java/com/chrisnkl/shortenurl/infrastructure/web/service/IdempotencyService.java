package com.chrisnkl.shortenurl.infrastructure.web.service;

import com.chrisnkl.shortenurl.infrastructure.cache.TwoTierCacheAdapter;
import com.chrisnkl.shortenurl.infrastructure.web.dto.create_url.CreateUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class IdempotencyService {

    private final TwoTierCacheAdapter cacheAdapter;
    private final ObjectMapper objectMapper;
    private static final String IDEMPOTENCY_PREFIX = "idempotency:";

    /**
     * Get cached response for an idempotency key using two-tier cache
     */
    public Optional<CreateUrlResponse> getCachedResponse(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }

        try {
            String key = IDEMPOTENCY_PREFIX + idempotencyKey;
            return cacheAdapter.get(key)
                    .map(serialized -> {
                        try {
                            return objectMapper.readValue(serialized, CreateUrlResponse.class);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(response -> response != null);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Store response for an idempotency key using two-tier cache
     */
    public void cacheResponse(String idempotencyKey, CreateUrlResponse response) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return;
        }

        try {
            String key = IDEMPOTENCY_PREFIX + idempotencyKey;
            String serialized = objectMapper.writeValueAsString(response);
            cacheAdapter.put(key, serialized);
        } catch (Exception e) {
            // Log but don't fail the request if caching fails
        }
    }
}



