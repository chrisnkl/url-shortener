package com.chrisnkl.shortenurl.infrastructure.persistence;

import com.chrisnkl.shortenurl.domain.model.Link;
import com.chrisnkl.shortenurl.domain.ports.out.UrlRepositoryPort;
import com.chrisnkl.shortenurl.infrastructure.persistence.jpa.SpringDataUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JpaUrlRepositoryAdapter implements UrlRepositoryPort {

    private final SpringDataUrlRepository repository;

    @Override
    public Link save(Link link) {
        return null;
    }

    @Override
    public Optional<Link> findByAlias(String alias) {
        return Optional.empty();
    }
}
