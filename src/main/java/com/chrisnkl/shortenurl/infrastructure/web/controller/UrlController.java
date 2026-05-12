package com.chrisnkl.shortenurl.infrastructure.web.controller;

import com.chrisnkl.shortenurl.domain.model.BackendResponse;
import com.chrisnkl.shortenurl.domain.model.Link;
import com.chrisnkl.shortenurl.domain.ports.in.CreateUrlUseCase;
import com.chrisnkl.shortenurl.domain.ports.in.RedirectUrlUseCase;
import com.chrisnkl.shortenurl.infrastructure.web.dto.create_url.CreateUrlRequest;
import com.chrisnkl.shortenurl.infrastructure.web.dto.create_url.CreateUrlResponse;
import com.chrisnkl.shortenurl.infrastructure.web.service.IdempotencyService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("v1/urls")
@RestController
public class UrlController {

    private final CreateUrlUseCase createUrlUseCase;
    private final RedirectUrlUseCase redirectUrlUseCase;
    private final IdempotencyService idempotencyService;

    @Value("${app.base-url}")
    private String BASE_SHORT_URL;


    private static final String IDEMPOTENCY_HEADER = "Idempotency-Key";

    @RateLimiter(name = "createUrl")
    @PostMapping
    public ResponseEntity<BackendResponse<CreateUrlResponse>> createUrl(
            @RequestHeader(value = IDEMPOTENCY_HEADER, required = false) String idempotencyKey,
            @RequestBody @Valid CreateUrlRequest createUrlRequest) {

        if (idempotencyKey != null) {
            Optional<CreateUrlResponse> cachedResponse = idempotencyService.getCachedResponse(idempotencyKey);
            if (cachedResponse.isPresent()) return ResponseEntity.status(HttpStatus.CREATED).body(new BackendResponse<>(HttpStatus.CREATED.value(), "Short url has been created successfully.", cachedResponse.get()));
        }

        // Create short URL with optional TTL
        Link link = createUrlUseCase.createShortUrl(createUrlRequest.originalUrl(), createUrlRequest.ttlInSeconds());

        CreateUrlResponse response = new CreateUrlResponse(
                link.alias(),
                BASE_SHORT_URL + link.alias(),
                link.originalUrl(),
                link.expiresAt()
        );

        if (idempotencyKey == null) idempotencyKey = UUID.randomUUID().toString();


        idempotencyService.cacheResponse(idempotencyKey, response);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(IDEMPOTENCY_HEADER, idempotencyKey)
                .body(new BackendResponse<>(HttpStatus.CREATED.value(), "Short url has been created successfully.", response));
    }

    @RateLimiter(name = "redirectUrl")
    @GetMapping("/{alias}")
    public ResponseEntity<Void> redirect(
            @PathVariable String alias,
            @RequestHeader(value = "User-Agent", defaultValue = "Unknown") String userAgent,
            @RequestHeader(value = "X-Forwarded-For", defaultValue = "Unknown") String ipAddress) {

        String originalUrl = redirectUrlUseCase.getOriginalUrl(alias, userAgent, ipAddress);
        log.info("Retrieved original URL: {} User IP: {}", originalUrl, ipAddress);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .build();

    }

}


