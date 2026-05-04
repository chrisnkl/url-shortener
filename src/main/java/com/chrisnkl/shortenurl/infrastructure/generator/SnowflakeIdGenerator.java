package com.chrisnkl.shortenurl.infrastructure.generator;

import com.chrisnkl.shortenurl.domain.ports.out.IdGeneratorPort;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdGenerator implements IdGeneratorPort {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final long EPOCH = 1714521600000L;
    private long lastTimestamp = -1L;
    private long sequence = 0L;
    private final long workerId = 1L;

    @Override
    public synchronized String generateUniqueAlias() {

        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Error! Clock moved backwards.");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & 4095;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        long id = ((timestamp - EPOCH) << 22) | (workerId << 12) | sequence;

        return encodeBase62(id);

    }

    private String encodeBase62(long num) {
        StringBuilder str = new StringBuilder();
        while (num > 0) {
            str.append(ALPHABET.charAt((int) (num%62)));
            num /= 62;
        }
        return str.reverse().toString();
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

}
