package com.chrisnkl.shortenurl.domain.ports.out;

public interface AnalyticsOutboxPort {

    void recordClick(String alias, String userAgent, String ipAddress);

}
