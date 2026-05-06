package com.chrisnkl.shortenurl.infrastructure.persistence;

import com.chrisnkl.shortenurl.domain.model.Link;
import com.chrisnkl.shortenurl.domain.ports.out.UrlRepositoryPort;
import com.chrisnkl.shortenurl.infrastructure.persistence.entity.LinkEntity;
import com.chrisnkl.shortenurl.infrastructure.persistence.jpa.SpringDataLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JpaLinkRepositoryAdapter implements UrlRepositoryPort {

    private final SpringDataLinkRepository repository;

    @Override
    public Link save(Link link) {
        LinkEntity entity = LinkEntity.fromDomain(link);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<Link> findByAlias(String alias) {

        return repository.findById(alias).map(LinkEntity::toDomain);

    }
}
