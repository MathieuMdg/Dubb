package com.lonelys.Dubb.repository;

import com.lonelys.Dubb.entity.Attempt;
import com.lonelys.Dubb.entity.Segment;
import com.lonelys.Dubb.entity.SegmentRecording;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SegmentRecordingRepository extends CrudRepository<SegmentRecording, Long> {
    List<SegmentRecording> findByAttempt(Attempt attempt);
    Optional<SegmentRecording> findByAttemptAndSegment(Attempt attempt, Segment segment);
}