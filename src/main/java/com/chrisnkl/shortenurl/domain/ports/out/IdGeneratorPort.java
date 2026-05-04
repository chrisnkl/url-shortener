package com.chrisnkl.shortenurl.domain.ports.out;

@FunctionalInterface
public interface IdGeneratorPort {

    String generateUniqueAlias();

}
