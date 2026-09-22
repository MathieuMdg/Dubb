package com.lonelys.Dubb.controller;

import com.lonelys.Dubb.dto.CreateMovieRequest;
import com.lonelys.Dubb.dto.DtoMapper;
import com.lonelys.Dubb.dto.MovieDto;
import com.lonelys.Dubb.entity.Movie;
import com.lonelys.Dubb.service.MovieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public MovieDto createMovie(@RequestBody CreateMovieRequest request) {
        Movie movie = movieService.createMovie(request.getMovieTitle(), request.getMovieYear(), request.getMovieDirector());
        return DtoMapper.toDto(movie);
    }

    @GetMapping("/{id}")
    public MovieDto getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        return DtoMapper.toDto(movie);
    }

    @GetMapping
    public List<MovieDto> getAllMovies() {
        return movieService.getAllMovies().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }
}