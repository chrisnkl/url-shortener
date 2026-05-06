package com.chrisnkl.shortenurl.infrastructure.web.controller;

import com.chrisnkl.shortenurl.domain.model.Link;
import com.chrisnkl.shortenurl.domain.ports.in.CreateUrlUseCase;
import com.chrisnkl.shortenurl.domain.ports.in.RedirectUrlUseCase;
import com.chrisnkl.shortenurl.infrastructure.web.dto.create_url.CreateUrlRequest;
import com.chrisnkl.shortenurl.infrastructure.web.dto.create_url.CreateUrlResponse;
import com.chrisnkl.shortenurl.infrastructure.web.service.IdempotencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/v1/urls")
@RestController
public class UrlController {

    private final CreateUrlUseCase createUrlUseCase;
    private final RedirectUrlUseCase redirectUrlUseCase;
    private final IdempotencyService idempotencyService;
    private static final String BASE_SHORT_URL = "http://localhost:8080/api/v1/urls/";

    @PostMapping
    public ResponseEntity<CreateUrlResponse> createUrl(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody @Valid CreateUrlRequest createUrlRequest) {

        Optional<CreateUrlResponse> cachedResponse = idempotencyService.getCachedResponse(idempotencyKey);
        if (cachedResponse.isPresent()) return ResponseEntity.status(HttpStatus.CREATED).body(cachedResponse.get());


        // Create short URL with optional TTL
        Link link = createUrlUseCase.createShortUrl(createUrlRequest.originalUrl(), createUrlRequest.ttlInSeconds());

        CreateUrlResponse response = buildResponse(link);

        // Cache the response for idempotency
        idempotencyService.cacheResponse(idempotencyKey, response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{alias}")
    public ResponseEntity<Void> redirect(
            @PathVariable String alias,
            @RequestHeader(value = "User-Agent", defaultValue = "Unknown") String userAgent,
            @RequestHeader(value = "X-Forwarded-For", defaultValue = "Unknown") String ipAddress) {

        String originalUrl = redirectUrlUseCase.getOriginalUrl(alias, userAgent, ipAddress);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .build();

    }

    private CreateUrlResponse buildResponse(Link link) {
        return new CreateUrlResponse(
            link.alias(),
            BASE_SHORT_URL + link.alias(),
            link.originalUrl(),
            link.expiresAt()
        );
    }
}


