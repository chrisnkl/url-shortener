package com.chrisnkl.shortenurl.infrastructure.persistence.jpa;

import com.chrisnkl.shortenurl.infrastructure.persistence.entity.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SpringDataLinkRepository extends JpaRepository<LinkEntity, String> {
}
