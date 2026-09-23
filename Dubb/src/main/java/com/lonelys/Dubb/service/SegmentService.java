package com.lonelys.Dubb.service;

import com.lonelys.Dubb.entity.Segment;
import com.lonelys.Dubb.entity.Clip;
import com.lonelys.Dubb.exception.InvalidSegmentException;
import com.lonelys.Dubb.repository.SegmentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SegmentService {

    //Attributes
    private static final Logger log = LoggerFactory.getLogger(SegmentService.class);
    private final SegmentRepository segmentRepository;

    //Constructors
    public SegmentService(SegmentRepository segmentRepository) {
        this.segmentRepository = segmentRepository;
    }

    //Methods
    //Create a Segment and add it to the database
    public Segment createSegment(Clip clip, double startTime, double endTime, int orderIndex, boolean dubbable, String label) {
        if (clip == null) {
            throw new InvalidSegmentException("Clip must not be null");
        }

        if (label == null || label.isBlank()) {
            label = "Segment " + orderIndex + " (" + startTime + "s-" + endTime + "s)";
        }

        Segment segment = new Segment(clip, startTime, endTime, orderIndex, dubbable, label);

        Segment savedSegment = segmentRepository.save(segment);
        log.info("Segment created {}", savedSegment);
        return savedSegment;
    }

    public List<Segment> getSegmentByClip(Clip clip) {
        if (clip == null) {
            throw new InvalidSegmentException("Clip must not be null");
        }
        return segmentRepository.findByClipOrderByOrderIndexAsc(clip);
    }

    public Segment getSegmentById(Long segmentId) {
        return segmentRepository.findById(segmentId).orElseThrow(() -> new InvalidSegmentException("Segment not found with id: " + segmentId));
    }

    //Check if Segment is valid
    private void checkSegment(double startTime, double endTime) {
        if (startTime < 0 || endTime < 0) {
            log.warn("Attempt to create a segment with negative time values");
            throw new InvalidSegmentException("Start and end time must be positive");
        }
        if (endTime <= startTime) {
            log.warn("Attempt to create a segment with endTime <= startTime");
            throw new InvalidSegmentException("End time must be after start time");
        }
    }
}