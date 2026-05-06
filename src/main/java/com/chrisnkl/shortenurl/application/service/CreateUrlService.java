package com.chrisnkl.shortenurl.application.service;

import com.chrisnkl.shortenurl.domain.model.Link;
import com.chrisnkl.shortenurl.domain.ports.in.CreateUrlUseCase;
import com.chrisnkl.shortenurl.domain.ports.out.CachePort;
import com.chrisnkl.shortenurl.domain.ports.out.IdGeneratorPort;
import com.chrisnkl.shortenurl.domain.ports.out.UrlRepositoryPort;
import lombok.RequiredArgsConstructor;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@RequiredArgsConstructor
public class CreateUrlService implements CreateUrlUseCase {

    private final UrlRepositoryPort urlRepositoryPort;
    private final IdGeneratorPort idGeneratorPort;
    private final CachePort cachePort;

    @Override
    public Link createShortUrl(String originalUrl, Long ttlInSeconds) {

        String alias = idGeneratorPort.generateUniqueAlias();

        // Calculate expiration time
        Instant expiresAt = ttlInSeconds != null
            ? Instant.now().plus(ttlInSeconds, ChronoUnit.SECONDS)
            : Instant.now().plus(365, ChronoUnit.DAYS);

        Link link = Link.create(alias, originalUrl, Instant.now(), expiresAt);

        urlRepositoryPort.save(link);

        cachePort.put(alias, originalUrl);

        return link;
    }
}
