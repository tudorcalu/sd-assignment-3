package org.example.assignment3.movie.api;

import org.example.assignment3.movie.model.MoviePageResponse;
import org.example.assignment3.movie.service.MoviePageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MoviePageService moviePageService;

    public MovieController(MoviePageService moviePageService) {
        this.moviePageService = moviePageService;
    }

    @GetMapping("/{movieId}/page")
    public MoviePageResponse getMoviePage(@PathVariable("movieId") String movieId) {
        return moviePageService.buildMoviePage(movieId);
    }
}
