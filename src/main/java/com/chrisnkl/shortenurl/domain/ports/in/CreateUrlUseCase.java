package com.chrisnkl.shortenurl.domain.ports.in;

@FunctionalInterface
public interface CreateUrlUseCase {

    String createShortUrl(String originalUrl);

}
