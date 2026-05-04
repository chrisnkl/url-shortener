package com.chrisnkl.shortenurl.domain.ports.out;

import com.chrisnkl.shortenurl.domain.model.Link;

import java.util.Optional;

public interface UrlRepositoryPort {

    Link save(Link link);
    Optional<Link> findByAlias(String alias);

}
