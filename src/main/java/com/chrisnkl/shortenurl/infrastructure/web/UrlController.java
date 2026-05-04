package com.chrisnkl.shortenurl.infrastructure.web;

import com.chrisnkl.shortenurl.domain.ports.in.CreateUrlUseCase;
import com.chrisnkl.shortenurl.domain.ports.in.RedirectUrlUseCase;
import com.chrisnkl.shortenurl.infrastructure.web.dto.create_url.CreateUrlRequest;
import com.chrisnkl.shortenurl.infrastructure.web.dto.create_url.CreateUrlResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/api/v1/urls")
@RestController
public class UrlController {

    private final CreateUrlUseCase createUrlUseCase;
    private final RedirectUrlUseCase redirectUrlUseCase;

    @PostMapping
    public ResponseEntity<CreateUrlResponse> createUrl(@RequestHeader(value = "Idempotency-Key") String idempotencyKey,
                                                       @RequestBody @Valid CreateUrlRequest createUrlRequest) {



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
}
