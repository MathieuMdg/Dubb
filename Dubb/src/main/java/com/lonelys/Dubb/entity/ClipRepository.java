package com.lonelys.Dubb.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClipRepository extends CrudRepository<Clip, Long> {

    List<Clip> findByClipTitle(String title);
    List<Clip> findBySourceMovie(Movie source);
}