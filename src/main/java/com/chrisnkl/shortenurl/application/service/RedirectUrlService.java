package com.chrisnkl.shortenurl.application.service;

import com.chrisnkl.shortenurl.domain.exception.UrlNotFoundException;
import com.chrisnkl.shortenurl.domain.model.Link;
import com.chrisnkl.shortenurl.domain.ports.in.RedirectUrlUseCase;
import com.chrisnkl.shortenurl.domain.ports.out.AnalyticsOutboxPort;
import com.chrisnkl.shortenurl.domain.ports.out.CachePort;
import com.chrisnkl.shortenurl.domain.ports.out.UrlRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RedirectUrlService implements RedirectUrlUseCase {

    private final CachePort cachePort;
    private final UrlRepositoryPort urlRepositoryPort;
    private final AnalyticsOutboxPort analyticsOutboxPort;

    @Override
    public String getOriginalUrl(String alias, String userAgent, String ipAddress) {

        return cachePort.get(alias)
                .map(url -> {
                    analyticsOutboxPort.recordClick(alias, userAgent, ipAddress);
                    return url;
                })
                .orElseGet(() -> {

                    Link link = urlRepositoryPort.findByAlias(alias).orElseThrow(() -> new UrlNotFoundException("Alias not found."));

                    cachePort.put(alias, link.originalUrl());

                    analyticsOutboxPort.recordClick(alias, userAgent, ipAddress);

                    return link.originalUrl();
                });

    }
}
