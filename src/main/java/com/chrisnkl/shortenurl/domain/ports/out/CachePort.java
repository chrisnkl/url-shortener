package com.chrisnkl.shortenurl.domain.ports.out;

import java.util.Optional;

public interface CachePort {

    void put(String key, String value);
    Optional<String> get(String key);

}
