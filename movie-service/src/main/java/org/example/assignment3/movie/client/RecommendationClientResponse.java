package org.example.assignment3.movie.client;

import java.util.List;

public record RecommendationClientResponse(String movieId, List<String> recommendations) {
}
