package com.chrisnkl.shortenurl.infrastructure.web.dto.create_url;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateUrlResponse(

        String alias,
        String shortUrl,
        String originalUrl,
        Instant expiresAt

) {
}
