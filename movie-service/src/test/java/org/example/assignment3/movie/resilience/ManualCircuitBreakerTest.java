package org.example.assignment3.movie.resilience;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ManualCircuitBreakerTest {

    private ManualCircuitBreaker breaker;

    @BeforeEach
    void setUp() {
        CircuitBreakerProperties properties = new CircuitBreakerProperties();
        properties.setSlidingWindowSize(4);
        properties.setMinimumNumberOfCalls(2);
        properties.setFailureRateThresholdPercent(50);
        properties.setWaitDurationInOpenStateMs(100);
        breaker = new ManualCircuitBreaker(properties);
    }

    @Test
    void opensAfterEnoughFailures() {
        assertThat(breaker.allowRequest()).isTrue();
        breaker.recordFailure();
        assertThat(breaker.getState()).isEqualTo(CircuitBreakerState.CLOSED);

        breaker.recordFailure();
        assertThat(breaker.getState()).isEqualTo(CircuitBreakerState.OPEN);
        assertThat(breaker.allowRequest()).isFalse();
    }

    @Test
    void recoversFromHalfOpenToClosedOnSuccess() throws InterruptedException {
        breaker.recordFailure();
        breaker.recordFailure();
        assertThat(breaker.getState()).isEqualTo(CircuitBreakerState.OPEN);

        Thread.sleep(150);
        assertThat(breaker.allowRequest()).isTrue();
        breaker.recordSuccess();

        assertThat(breaker.getState()).isEqualTo(CircuitBreakerState.CLOSED);
        assertThat(breaker.allowRequest()).isTrue();
    }
}
