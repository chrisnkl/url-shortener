package com.chrisnkl.shortenurl.domain.ports.in;

@FunctionalInterface
public interface RedirectUrlUseCase {

    String getOriginalUrl(String alias, String userAgent, String ipAddress);

}
