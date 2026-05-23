package org.example.assignment3.recommendation.service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ChaosModeService {

    private final boolean chaosMode;
    private final int failureRatePercent;
    private final int minLatencyMs;
    private final int maxLatencyMs;

    public ChaosModeService(
            @Value("${chaos.mode:false}") boolean chaosMode,
            @Value("${chaos.failure-rate-percent:35}") int failureRatePercent,
            @Value("${chaos.min-latency-ms:3000}") int minLatencyMs,
            @Value("${chaos.max-latency-ms:10000}") int maxLatencyMs) {
        this.chaosMode = chaosMode;
        this.failureRatePercent = failureRatePercent;
        this.minLatencyMs = minLatencyMs;
        this.maxLatencyMs = maxLatencyMs;
    }

    public void applyIfEnabled() {
        if (!chaosMode) {
            return;
        }

        int delay = ThreadLocalRandom.current().nextInt(minLatencyMs, maxLatencyMs + 1);
        sleep(Duration.ofMillis(delay));

        int coin = ThreadLocalRandom.current().nextInt(100);
        if (coin < failureRatePercent) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Chaos mode injected failure");
        }
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Chaos delay interrupted");
        }
    }
}
