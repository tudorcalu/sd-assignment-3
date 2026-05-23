package org.example.assignment3.recommendation.api;

import java.util.List;

public record RecommendationResponse(String movieId, List<String> recommendations) {
}
