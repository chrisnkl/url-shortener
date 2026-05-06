package com.chrisnkl.shortenurl.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class OutboxRelay {

    private final String selectQuery = "SELECT id,payload FROM outbox_events WHERE processed = false LIMIT 50";
    private final String updateQuery = "UPDATE outbox_events SET processed = true WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;


    @Scheduled(fixedDelay = 2000)
    public void relayEvents() {

        log.info("Relaying outbox events...");
        List<OutboxEvent> events = jdbcTemplate.query(selectQuery, (rs, rowNum) -> new OutboxEvent(rs.getString("id"), rs.getString("payload")));

        events.forEach(event -> {
            try {

                // Push to message broker
                publishToMessageBroker(event.payload());

                // Mark as processed
                jdbcTemplate.update(updateQuery, event.id());

            } catch (Exception e) {
                log.error("Failed to relay outbox event {}: {}", event.id(), e.getMessage());
            }
        });

    }


    private void publishToMessageBroker(String payload) {
        log.info("Relaying to analytics cluster: {}", payload);
    }

    private record OutboxEvent(String id, String payload) {}

}
