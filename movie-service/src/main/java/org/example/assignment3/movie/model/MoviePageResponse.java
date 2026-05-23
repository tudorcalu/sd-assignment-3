package org.example.assignment3.movie.model;

import java.util.List;

public record MoviePageResponse(
        Movie movie,
        List<Movie> recommendedMovies,
        String recommendationSource
) {
}
