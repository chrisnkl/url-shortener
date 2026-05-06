package com.chrisnkl.shortenurl.infrastructure.messaging;

import com.chrisnkl.shortenurl.domain.ports.out.AnalyticsOutboxPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class OutboxAdapter implements AnalyticsOutboxPort {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    private final String insertQuery = "INSERT INTO outbox_events(id, aggregate_type, payload, processed) VALUES (?,?,?,?)";

    @Override
    public void recordClick(String alias, String userAgent, String ipAddress) {

        log.info("Logging click: {} {} {}", alias, userAgent, ipAddress);

        try {
            String payload = objectMapper.writeValueAsString(OutboxRecord.create(alias, userAgent, ipAddress));

            jdbcTemplate.update(insertQuery, new Object[] {
                    UUID.randomUUID().toString(),
                    "URL_CLICK",
                    payload,
                    false
            });
        } catch (Exception e) {
            log.error("Failed to record click event: {}, error: {}... Continuing with the redirect..", alias, e.getMessage());
        }

    }

    private record OutboxRecord(String alias, String userAgent, String ipAddress, Instant timestamp) {

        public static OutboxRecord create(String alias, String userAgent, String ipAddress) {
            return new OutboxRecord(
                    alias,
                    userAgent,
                    ipAddress,
                    Instant.now()
            );
        }

    }

}
