package com.lonelys.Dubb.repository;

import com.lonelys.Dubb.entity.Clip;
import com.lonelys.Dubb.entity.Movie;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClipRepository extends CrudRepository<Clip, Long> {

    List<Clip> findByClipTitle(String title);
    List<Clip> findBySourceMovie(Movie source);

}