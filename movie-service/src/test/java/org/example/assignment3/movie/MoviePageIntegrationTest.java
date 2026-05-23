package org.example.assignment3.movie;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.example.assignment3.movie.model.MoviePageResponse;
import org.example.assignment3.movie.resilience.CircuitBreakerState;
import org.example.assignment3.movie.resilience.ManualCircuitBreaker;
import org.example.assignment3.movie.service.MoviePageService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class MoviePageIntegrationTest {

    private static MockWebServer mockRecommendationService;

    @Autowired
    private MoviePageService moviePageService;

    @Autowired
    private ManualCircuitBreaker circuitBreaker;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockRecommendationService = new MockWebServer();
        mockRecommendationService.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockRecommendationService.shutdown();
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("services.recommendation.base-url", () -> mockRecommendationService.url("/").toString());
        registry.add("circuit-breaker.wait-duration-in-open-state-ms", () -> "50");
    }

    @Test
    void returnsRecommendationsWhenDownstreamIsHealthy() {
        circuitBreaker.reset();
        mockRecommendationService.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {"movieId":"1","recommendations":["2","3","4"]}
                        """));

        MoviePageResponse response = moviePageService.buildMoviePage("1");

        assertThat(response).isNotNull();
        assertThat(response.recommendationSource()).isEqualTo("recommendation-service");
        assertThat(response.recommendedMovies()).hasSize(3);
    }

    @Test
    void fallsBackWhenDownstreamIsTooSlow() {
        circuitBreaker.reset();
        mockRecommendationService.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {"movieId":"1","recommendations":["2","3","4"]}
                        """)
                .setBodyDelay(2, TimeUnit.SECONDS));

        MoviePageResponse response = moviePageService.buildMoviePage("1");

        assertThat(response).isNotNull();
        assertThat(response.recommendationSource()).isEqualTo("fallback-trending");
        assertThat(response.recommendedMovies()).extracting("id").containsExactly("7", "8", "9");
    }

    @Test
    void recoversCircuitToClosedStateWhenBackendIsHealthyAgain() throws InterruptedException {
        circuitBreaker.reset();
        circuitBreaker.recordFailure();
        circuitBreaker.recordFailure();

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreakerState.OPEN);

        Thread.sleep(60);

        assertThat(circuitBreaker.allowRequest()).isTrue();
        circuitBreaker.recordSuccess();

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreakerState.CLOSED);
    }
}
