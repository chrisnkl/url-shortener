package com.chrisnkl.shortenurl.infrastructure.config;

import com.chrisnkl.shortenurl.application.service.CreateUrlService;
import com.chrisnkl.shortenurl.application.service.RedirectUrlService;
import com.chrisnkl.shortenurl.domain.ports.out.AnalyticsOutboxPort;
import com.chrisnkl.shortenurl.domain.ports.out.CachePort;
import com.chrisnkl.shortenurl.domain.ports.out.IdGeneratorPort;
import com.chrisnkl.shortenurl.domain.ports.out.UrlRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public CreateUrlService createUrlUseCase(
            UrlRepositoryPort repository,
            IdGeneratorPort idGenerator,
            CachePort cache) {
        return new CreateUrlService(repository, idGenerator, cache);
    }

    @Bean
    public RedirectUrlService redirectUrlUseCase(
            UrlRepositoryPort repository,
            CachePort cache,
            AnalyticsOutboxPort outboxPort) {
        return new RedirectUrlService(cache, repository, outboxPort);
    }

}
