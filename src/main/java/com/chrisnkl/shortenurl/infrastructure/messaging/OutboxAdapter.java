package com.chrisnkl.shortenurl.infrastructure.messaging;

import com.chrisnkl.shortenurl.domain.ports.out.AnalyticsOutboxPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OutboxAdapter implements AnalyticsOutboxPort {

    @Override
    public void recordClick(String alias, String userAgent, String ipAddress) {

    }
}
