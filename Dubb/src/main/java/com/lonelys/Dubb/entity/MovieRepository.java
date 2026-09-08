package com.lonelys.Dubb.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {

    List<Movie> findByMovieTitle(String title);
    List<Movie> findByMovieDirector(String director);
    List<Movie> findByMovieYear(int year);
}