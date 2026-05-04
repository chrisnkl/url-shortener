package com.chrisnkl.shortenurl.application.service;

import com.chrisnkl.shortenurl.domain.model.Link;
import com.chrisnkl.shortenurl.domain.ports.in.CreateUrlUseCase;
import com.chrisnkl.shortenurl.domain.ports.out.CachePort;
import com.chrisnkl.shortenurl.domain.ports.out.IdGeneratorPort;
import com.chrisnkl.shortenurl.domain.ports.out.UrlRepositoryPort;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class CreateUrlService implements CreateUrlUseCase {


    private final UrlRepositoryPort urlRepositoryPort;
    private final IdGeneratorPort idGeneratorPort;
    private final CachePort cachePort;

    @Override
    public String createShortUrl(String originalUrl) {

        String alias = idGeneratorPort.generateUniqueAlias();

        Link link = Link.create(alias, originalUrl);

        urlRepositoryPort.save(link);

        cachePort.put(alias, originalUrl);


        return alias;
    }
}
