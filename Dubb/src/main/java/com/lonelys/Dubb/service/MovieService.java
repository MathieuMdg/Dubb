package com.lonelys.Dubb.service;

import com.lonelys.Dubb.entity.Movie;
import com.lonelys.Dubb.exception.InvalidMovieException;
import com.lonelys.Dubb.exception.MovieNotFoundException;
import com.lonelys.Dubb.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MovieService {

    //Attributes
    private static final Logger log = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;

    //Constructor
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    //Methods
    public Movie createMovie(String title, int year, String director){
        checkMovieData(title, year, director);
        Movie movie = new Movie(title, year, director);
        Movie savedMovie = movieRepository.save(movie);
        log.info("New Movie added : {}", savedMovie);
        return savedMovie;
    }

    public Movie getMovieById(Long id){
        return movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException("Movie with id " + id + " not found"));
    }

    public List<Movie> getAllMovies(){
        return (List<Movie>) movieRepository.findAll();
    }

    private void checkMovieData(String title, int year, String director) {
        if (title == null || title.isBlank()) {
            throw new InvalidMovieException("Title must not be blank");
        }
        if (director == null || director.isBlank()) {
            throw new InvalidMovieException("Director must not be blank");
        }
        if (year < 1888 || year > LocalDate.now().getYear() + 1) {
            throw new InvalidMovieException("Year is not plausible: " + year);
        }
    }
}
