package org.example.assignment3.recommendation.api;

import org.example.assignment3.recommendation.service.ChaosModeService;
import org.example.assignment3.recommendation.service.RecommendationEngine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationEngine recommendationEngine;
    private final ChaosModeService chaosModeService;

    public RecommendationController(RecommendationEngine recommendationEngine, ChaosModeService chaosModeService) {
        this.recommendationEngine = recommendationEngine;
        this.chaosModeService = chaosModeService;
    }

    @GetMapping("/{movieId}")
    public RecommendationResponse getRecommendations(@PathVariable("movieId") String movieId) {
        chaosModeService.applyIfEnabled();
        return new RecommendationResponse(movieId, recommendationEngine.getRecommendations(movieId));
    }
}
