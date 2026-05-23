package org.example.assignment3.movie.model;

import java.util.List;

public record RecommendationResult(List<String> movieIds, String source) {
}
