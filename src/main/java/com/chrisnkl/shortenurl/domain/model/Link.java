package com.chrisnkl.shortenurl.domain.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public record Link(


        String alias,
        String originalUrl,
        Instant createdAt,
        Instant expiresAt

) {

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }

    public static Link create(String alias, String originalUrl, Instant createdAt, Instant expiresAt) {
        return new Link(alias, originalUrl, createdAt, expiresAt);
    }

    public static Link create(String alias, String originalUrl) {
        return new Link(alias, originalUrl, Instant.now(), Instant.now().plus(365, ChronoUnit.DAYS));
    }

}
