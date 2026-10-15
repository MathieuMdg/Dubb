package com.lonelys.Dubb.service;

import com.lonelys.Dubb.entity.Segment;
import com.lonelys.Dubb.entity.Clip;
import com.lonelys.Dubb.entity.SegmentRecording;
import com.lonelys.Dubb.exception.InvalidSegmentException;
import com.lonelys.Dubb.repository.ClipRepository;
import com.lonelys.Dubb.repository.SegmentRecordingRepository;
import com.lonelys.Dubb.repository.SegmentRepository;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SegmentService {

    //Attributes
    private static final Logger log = LoggerFactory.getLogger(SegmentService.class);
    private final SegmentRepository segmentRepository;
    private final SegmentRecordingRepository segmentRecordingRepository;
    private final StorageService storageService;

    //Constructors
    public SegmentService(SegmentRepository segmentRepository, SegmentRecordingRepository segmentRecordingRepository, StorageService storageService) {
        this.segmentRepository = segmentRepository;
        this.segmentRecordingRepository = segmentRecordingRepository;
        this.storageService = storageService;
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

    private void checkNoOverlap(Clip clip, Long ignoredSegmentId, double start, double end) {
        for (Segment other : segmentRepository.findByClipOrderByOrderIndexAsc(clip)) {
            if (other.getSegmentID().equals(ignoredSegmentId)) continue;
            if (start < other.getEndTime() && end > other.getStartTime()) {
                log.warn("Segment overlap with segment {}", other.getSegmentID());
                throw new InvalidSegmentException("Segment overlaps an existing segment ("
                        + other.getStartTime() + "s - " + other.getEndTime() + "s)");
            }
        }
    }

    @Transactional
    public Segment updateSegment(Long segmentId, double start, double end,
                                 int orderIndex, boolean dubbable, String label) {
        Segment segment = getSegmentById(segmentId);
        checkSegment(start, end);
        checkNoOverlap(segment.getClip(), segmentId, start, end);

        segment.setStartTime(start);
        segment.setEndTime(end);
        segment.setOrderIndex(orderIndex);
        segment.setDubbable(dubbable);
        segment.setLabel(label);
        return segmentRepository.save(segment);
    }

    @Transactional
    public void deleteSegment(Long segmentId) {
        Segment segment = getSegmentById(segmentId);

        List<SegmentRecording> recordings = segmentRecordingRepository.findBySegment(segment);
        List<String> audioPaths = recordings.stream()
                .map(SegmentRecording::getAudioFilePath)
                .collect(Collectors.toList());

        segmentRecordingRepository.deleteAll(recordings);
        segmentRecordingRepository.flush();

        // Keep the parent's collection in sync, otherwise cascade can try to re-persist the segment
        segment.getClip().getSegments().remove(segment);
        segmentRepository.delete(segment);

        // Files are deleted last, so a database failure does not leave rows pointing to missing files
        audioPaths.forEach(storageService::delete); // use the real name of your delete method

        log.info("Segment {} deleted with {} recording(s)", segmentId, recordings.size());
    }
}