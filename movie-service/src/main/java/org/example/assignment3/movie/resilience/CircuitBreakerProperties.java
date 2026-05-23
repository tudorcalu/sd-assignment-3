package org.example.assignment3.movie.resilience;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "circuit-breaker")
public class CircuitBreakerProperties {

    private int slidingWindowSize = 4;
    private int minimumNumberOfCalls = 2;
    private int failureRateThresholdPercent = 50;
    private long waitDurationInOpenStateMs = 3000;

    public int getSlidingWindowSize() {
        return slidingWindowSize;
    }

    public void setSlidingWindowSize(int slidingWindowSize) {
        this.slidingWindowSize = slidingWindowSize;
    }

    public int getMinimumNumberOfCalls() {
        return minimumNumberOfCalls;
    }

    public void setMinimumNumberOfCalls(int minimumNumberOfCalls) {
        this.minimumNumberOfCalls = minimumNumberOfCalls;
    }

    public int getFailureRateThresholdPercent() {
        return failureRateThresholdPercent;
    }

    public void setFailureRateThresholdPercent(int failureRateThresholdPercent) {
        this.failureRateThresholdPercent = failureRateThresholdPercent;
    }

    public long getWaitDurationInOpenStateMs() {
        return waitDurationInOpenStateMs;
    }

    public void setWaitDurationInOpenStateMs(long waitDurationInOpenStateMs) {
        this.waitDurationInOpenStateMs = waitDurationInOpenStateMs;
    }
}
