package com.lonelys.Dubb.service;

import com.lonelys.Dubb.entity.*;
import com.lonelys.Dubb.exception.InvalidSegmentException;
import com.lonelys.Dubb.exception.InvalidSegmentRecordingException;
import com.lonelys.Dubb.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class SegmentRecordingService {

    //Attributes
    private static final Logger log = LoggerFactory.getLogger(SegmentRecordingService.class);

    private final SegmentRecordingRepository segmentRecordingRepository;
    private final SegmentRepository segmentRepository;
    private final AttemptRepository attemptRepository;
    private final StorageService storageService;

    //Constructors
    public SegmentRecordingService(SegmentRecordingRepository segmentRecordingRepository, SegmentRepository segmentRepository, AttemptRepository attemptRepository, StorageService storageService) {
        this.segmentRecordingRepository = segmentRecordingRepository;
        this.segmentRepository = segmentRepository;
        this.attemptRepository = attemptRepository;
        this.storageService = storageService;
    }

    //Methods
    //Save a segmentRecording
    public SegmentRecording recordSegment(Long attemptId, Long segmentId, MultipartFile audio) {

        Attempt attempt = attemptRepository.findById(attemptId).orElseThrow(() -> new InvalidSegmentRecordingException("Attempt not found with id: " + attemptId));
        Segment segment = segmentRepository.findById(segmentId).orElseThrow(() -> new InvalidSegmentException("Segment not found with id: " + segmentId));

        checkIfAttemptInProgress(attempt);
        checkIfSegmentDubbable(segment);

        Optional<SegmentRecording> alreadyExist = segmentRecordingRepository.findByAttemptAndSegment(attempt, segment);

        if (alreadyExist.isPresent()) {
            log.info("Existing recording found for attempt {} and segment {}, replacing it", attemptId, segmentId);
            storageService.delete(alreadyExist.get().getAudioFilePath());
        }

        String subFolder = "attempts/" + attemptId;
        String audioPath = storageService.save(audio, subFolder);

        SegmentRecording recording = alreadyExist.orElseGet(SegmentRecording::new);
        recording.setAttempt(attempt);
        recording.setSegment(segment);
        recording.setAudioFilePath(audioPath);

        SegmentRecording savedRecording = segmentRecordingRepository.save(recording);

        log.info("SegmentRecording saved for attempt {} and segment {} : {}", attemptId, segmentId, savedRecording);
        return savedRecording;
    }

    //List all Segment from an attempt
    public List<SegmentRecording> getAllRecordsByAttempt(Long attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId).orElseThrow(() -> new InvalidSegmentRecordingException("Attempt not found with id: " + attemptId));
        return segmentRecordingRepository.findByAttempt(attempt);
    }

    //Check if an attempt status is "IN_PROGRESS"
    private void checkIfAttemptInProgress(Attempt attempt) {
        if(!attempt.getStatus().equals("IN_PROGRESS")){
            log.warn("Attempt to create a segmentRecording on a Attempt whitout status IN_PROGRESS");
            throw new InvalidSegmentRecordingException("Attempt must be IN_PROGRESS");
        }
    }

    //Check if a segment is dubbable
    private void checkIfSegmentDubbable(Segment segment) {
        if (!segment.isDubbable()) {
            log.warn("Attempt to create a segmentRecording on a non-dubbable segment");
            throw new InvalidSegmentRecordingException("Segment must be dubbable");
        }
    }
}