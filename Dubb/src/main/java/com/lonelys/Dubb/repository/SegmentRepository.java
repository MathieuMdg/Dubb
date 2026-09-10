package com.lonelys.Dubb.repository;

import com.lonelys.Dubb.entity.Clip;
import com.lonelys.Dubb.entity.Segment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SegmentRepository extends CrudRepository<Segment, Long> {
    List<Segment> findByClipOrderByOrderIndexAsc(Clip clip);
}