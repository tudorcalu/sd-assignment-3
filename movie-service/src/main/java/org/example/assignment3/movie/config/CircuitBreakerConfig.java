package org.example.assignment3.movie.config;

import org.example.assignment3.movie.resilience.CircuitBreakerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CircuitBreakerProperties.class)
public class CircuitBreakerConfig {
}
