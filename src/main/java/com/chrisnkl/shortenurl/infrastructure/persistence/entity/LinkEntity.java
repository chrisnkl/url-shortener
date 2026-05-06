package com.chrisnkl.shortenurl.infrastructure.persistence.entity;

import com.chrisnkl.shortenurl.domain.model.Link;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
@Table(name = "links")
@Entity
public class LinkEntity {

    @Id
    @Column(name = "alias", unique = true)
    private String alias;

    @Column(name = "original_url")
    private String originalUrl;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    public Link toDomain() {
        return Link.create(alias, originalUrl, createdAt, expiresAt);
    }

    public static LinkEntity fromDomain(Link link) {
        return new LinkEntity(
                link.alias(),
                link.originalUrl(),
                link.createdAt(),
                link.expiresAt());
    }

}
