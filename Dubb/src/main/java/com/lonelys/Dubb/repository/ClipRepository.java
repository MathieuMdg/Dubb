package com.lonelys.Dubb.repository;

import com.lonelys.Dubb.entity.Clip;
import com.lonelys.Dubb.entity.Movie;
import org.springframework.data.repository.CrudRepository;

public interface ClipRepository extends CrudRepository<Clip, Long> {

    Iterable<Clip> findByClipTitle(String title);
    Iterable<Clip> findBySourceMovie(Movie source);

}