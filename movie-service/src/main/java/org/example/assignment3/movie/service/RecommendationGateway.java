package org.example.assignment3.movie.service;

import java.util.List;
import org.example.assignment3.movie.client.RecommendationClientResponse;
import org.example.assignment3.movie.model.RecommendationResult;
import org.example.assignment3.movie.resilience.ManualCircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RecommendationGateway {

    private static final List<String> TRENDING_FALLBACK_IDS = List.of("7", "8", "9");
    private final RestClient recommendationRestClient;
    private final ManualCircuitBreaker circuitBreaker;

    public RecommendationGateway(RestClient recommendationRestClient, ManualCircuitBreaker circuitBreaker) {
        this.recommendationRestClient = recommendationRestClient;
        this.circuitBreaker = circuitBreaker;
    }

    public RecommendationResult fetchRecommendedMovieIds(String movieId) {
        if (!circuitBreaker.allowRequest()) {
            return trendingFallback();
        }

        try {
            RecommendationResult result = callRecommendationService(movieId);
            circuitBreaker.recordSuccess();
            return result;
        } catch (RuntimeException ex) {
            circuitBreaker.recordFailure();
            return trendingFallback();
        }
    }

    private RecommendationResult callRecommendationService(String movieId) {
        RecommendationClientResponse response = recommendationRestClient.get()
                .uri("/api/recommendations/{movieId}", movieId)
                .retrieve()
                .body(RecommendationClientResponse.class);

        List<String> recommendations = response == null || response.recommendations() == null
                ? TRENDING_FALLBACK_IDS
                : response.recommendations();
        return new RecommendationResult(recommendations, "recommendation-service");
    }

    private RecommendationResult trendingFallback() {
        return new RecommendationResult(TRENDING_FALLBACK_IDS, "fallback-trending");
    }
}
