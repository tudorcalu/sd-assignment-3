package org.example.assignment3.movie.resilience;

import java.util.ArrayDeque;
import java.util.Deque;
import org.springframework.stereotype.Component;

@Component
public class ManualCircuitBreaker {

    private final CircuitBreakerProperties properties;
    private final Deque<Boolean> outcomes = new ArrayDeque<>();
    private final Object lock = new Object();

    private CircuitBreakerState state = CircuitBreakerState.CLOSED;
    private long openedAtEpochMs;

    public ManualCircuitBreaker(CircuitBreakerProperties properties) {
        this.properties = properties;
    }

    public boolean allowRequest() {
        synchronized (lock) {
            if (state == CircuitBreakerState.OPEN) {
                if (elapsedSinceOpen() >= properties.getWaitDurationInOpenStateMs()) {
                    state = CircuitBreakerState.HALF_OPEN;
                    return true;
                }
                return false;
            }
            return true;
        }
    }

    public void recordSuccess() {
        synchronized (lock) {
            if (state == CircuitBreakerState.HALF_OPEN) {
                state = CircuitBreakerState.CLOSED;
                outcomes.clear();
                return;
            }
            recordOutcome(true);
        }
    }

    public void recordFailure() {
        synchronized (lock) {
            if (state == CircuitBreakerState.HALF_OPEN) {
                transitionToOpen();
                return;
            }
            recordOutcome(false);
            if (shouldOpenCircuit()) {
                transitionToOpen();
            }
        }
    }

    public CircuitBreakerState getState() {
        synchronized (lock) {
            if (state == CircuitBreakerState.OPEN
                    && elapsedSinceOpen() >= properties.getWaitDurationInOpenStateMs()) {
                return CircuitBreakerState.HALF_OPEN;
            }
            return state;
        }
    }

    public void reset() {
        synchronized (lock) {
            state = CircuitBreakerState.CLOSED;
            outcomes.clear();
            openedAtEpochMs = 0L;
        }
    }

    private void recordOutcome(boolean success) {
        outcomes.addLast(success);
        while (outcomes.size() > properties.getSlidingWindowSize()) {
            outcomes.removeFirst();
        }
    }

    private boolean shouldOpenCircuit() {
        if (outcomes.size() < properties.getMinimumNumberOfCalls()) {
            return false;
        }
        long failures = outcomes.stream().filter(success -> !success).count();
        int failureRatePercent = (int) ((failures * 100) / outcomes.size());
        return failureRatePercent >= properties.getFailureRateThresholdPercent();
    }

    private void transitionToOpen() {
        state = CircuitBreakerState.OPEN;
        openedAtEpochMs = System.currentTimeMillis();
    }

    private long elapsedSinceOpen() {
        return System.currentTimeMillis() - openedAtEpochMs;
    }
}
