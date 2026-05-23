package org.example.assignment3.movie.service;

import java.util.List;
import java.util.Map;
import org.example.assignment3.movie.model.Movie;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MovieCatalogService {

    private static final Map<String, Movie> MOVIES = Map.of(
            "1", new Movie("1", "Interstellar", "A team travels through a wormhole to save humanity."),
            "2", new Movie("2", "Inception", "A thief enters dreams to steal and plant ideas."),
            "3", new Movie("3", "The Matrix", "A hacker discovers reality is a simulation."),
            "4", new Movie("4", "Arrival", "A linguist decodes alien language to prevent war."),
            "5", new Movie("5", "Dune", "A noble family battles for control of a desert planet."),
            "6", new Movie("6", "Blade Runner 2049", "A replicant hunter uncovers a buried secret."),
            "7", new Movie("7", "Top Trending One", "Trending fallback movie one."),
            "8", new Movie("8", "Top Trending Two", "Trending fallback movie two."),
            "9", new Movie("9", "Top Trending Three", "Trending fallback movie three.")
    );

    public Movie getByIdOrThrow(String movieId) {
        Movie movie = MOVIES.get(movieId);
        if (movie == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found: " + movieId);
        }
        return movie;
    }

    public List<Movie> getByIds(List<String> ids) {
        return ids.stream()
                .map(id -> MOVIES.getOrDefault(id, new Movie(id, "Unknown Movie " + id, "No description available.")))
                .toList();
    }
}
