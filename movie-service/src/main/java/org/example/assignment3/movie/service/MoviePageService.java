package org.example.assignment3.movie.service;

import org.example.assignment3.movie.model.Movie;
import org.example.assignment3.movie.model.MoviePageResponse;
import org.example.assignment3.movie.model.RecommendationResult;
import org.springframework.stereotype.Service;

@Service
public class MoviePageService {

    private final MovieCatalogService movieCatalogService;
    private final RecommendationGateway recommendationGateway;

    public MoviePageService(MovieCatalogService movieCatalogService, RecommendationGateway recommendationGateway) {
        this.movieCatalogService = movieCatalogService;
        this.recommendationGateway = recommendationGateway;
    }

    public MoviePageResponse buildMoviePage(String movieId) {
        Movie movie = movieCatalogService.getByIdOrThrow(movieId);
        RecommendationResult recommendationResult = recommendationGateway.fetchRecommendedMovieIds(movieId);

        return new MoviePageResponse(
                movie,
                movieCatalogService.getByIds(recommendationResult.movieIds()),
                recommendationResult.source()
        );
    }
}
