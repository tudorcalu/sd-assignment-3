package org.example.assignment3.movie.resilience;

public enum CircuitBreakerState {
    CLOSED,
    OPEN,
    HALF_OPEN
}
