package com.chrisnkl.shortenurl.domain.ports.in;

import com.chrisnkl.shortenurl.domain.model.Link;

@FunctionalInterface
public interface CreateUrlUseCase {

    Link createShortUrl(String originalUrl, Long ttlInSeconds);

}
