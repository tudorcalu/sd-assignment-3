package org.example.assignment3.recommendation.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RecommendationEngine {

    private static final Map<String, List<String>> RECOMMENDATIONS = Map.of(
            "1", List.of("2", "3", "4"),
            "2", List.of("1", "3", "5"),
            "3", List.of("1", "2", "6"),
            "4", List.of("1", "5", "6"),
            "5", List.of("2", "4", "6"),
            "6", List.of("1", "4", "5"),
            "7", List.of("2", "3", "8"),
            "8", List.of("4", "5", "9"),
            "9", List.of("1", "6", "7")
    );

    public List<String> getRecommendations(String movieId) {
        return RECOMMENDATIONS.getOrDefault(movieId, List.of("4", "5", "6"));
    }
}
