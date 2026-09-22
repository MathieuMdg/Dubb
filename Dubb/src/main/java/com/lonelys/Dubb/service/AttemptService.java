package com.lonelys.Dubb.service;

import com.lonelys.Dubb.dto.AttemptProgressDto;
import com.lonelys.Dubb.entity.*;
import com.lonelys.Dubb.exception.*;
import com.lonelys.Dubb.repository.*;

import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttemptService {

    // Attributes
    private static final Logger log = LoggerFactory.getLogger(AttemptService.class);

    private final AttemptRepository attemptRepository;
    private final UserRepository userRepository;
    private final ClipRepository clipRepository;

    // Constructor
    public AttemptService(AttemptRepository attemptRepository, UserRepository userRepository, ClipRepository clipRepository) {
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
        this.clipRepository = clipRepository;
    }

    // Methods
    public Attempt startAttempt(Long userId, Long clipId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidUserException("User not found with id: " + userId));
        Clip clip = clipRepository.findById(clipId).orElseThrow(() -> new InvalidClipException("Clip not found with id: " + clipId));

        Attempt attempt = new Attempt(user, clip, "IN_PROGRESS", LocalDateTime.now());
        Attempt savedAttempt = attemptRepository.save(attempt);

        log.info("Attempt created {}", savedAttempt);
        return savedAttempt;
    }

    @Transactional(readOnly = true)
    public AttemptProgressDto getProgression(Long attemptId) {

        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new InvalidAttemptException("Attempt not found with id: " + attemptId));

        List<Segment> segmentsDubbables = attempt.getClip().getSegments().stream()
                .filter(Segment::isDubbable)
                .collect(Collectors.toList());

        List<Long> segmentsEnregistresIds = attempt.getRecordings().stream()
                .map(recording -> recording.getSegment().getSegmentID())
                .collect(Collectors.toList());

        List<Long> segmentsManquantsIds = segmentsDubbables.stream()
                .map(Segment::getSegmentID)
                .filter(id -> !segmentsEnregistresIds.contains(id))
                .collect(Collectors.toList());

        boolean complete = segmentsManquantsIds.isEmpty();

        return new AttemptProgressDto(
                segmentsDubbables.size(),
                segmentsDubbables.size() - segmentsManquantsIds.size(),
                segmentsManquantsIds,
                complete
        );
    }

    @Transactional
    public Attempt finaliseAttempt(Long attemptId) {

        Attempt attempt = attemptRepository.findById(attemptId).orElseThrow(() -> new InvalidAttemptException("Attempt not found with id: " + attemptId));

        if (!attempt.getStatus().equals("IN_PROGRESS")) {
            log.warn("Attempt to finalize an attempt that is not IN_PROGRESS (id: {})", attemptId);
            throw new InvalidAttemptException("Attempt must be IN_PROGRESS to be finalized");
        }

        checkAllSegmentRecordings(attempt);

        // String finalVideoPath = ffmpegService.assemblerVideo(attempt);
        // attempt.setFinalVideoPath(finalVideoPath);

        attempt.setStatus("COMPLETED");
        attempt.setCompletedAt(LocalDateTime.now());

        Attempt savedAttempt = attemptRepository.save(attempt);
        log.info("Attempt finalized {}", savedAttempt);
        return savedAttempt;
    }

    private void checkAllSegmentRecordings(Attempt attempt) {

        long numberDubbableSegment = attempt.getClip().getSegments().stream().filter(Segment::isDubbable).count();
        long numberRecordings = attempt.getRecordings().size();

        if (numberDubbableSegment != numberRecordings) {
            log.warn("Attempt {} incomplete: {} dubbable segments, {} recordings",
                    attempt.getAttemptID(), numberDubbableSegment, numberRecordings);
            throw new InvalidAttemptException("All dubbable segments must have a recording before finalizing");
        }
    }

    public Attempt getAttemptById(Long attemptId) {
        return attemptRepository.findById(attemptId)
                .orElseThrow(() -> new InvalidAttemptException("Attempt not found with id: " + attemptId));
    }

    public String getFinalVideoPath(Long attemptId) {
        Attempt attempt = getAttemptById(attemptId);
        if (attempt.getFinalVideoPath() == null) {
            throw new InvalidAttemptException("Attempt " + attemptId + " has no final video yet");
        }
        return attempt.getFinalVideoPath();
    }
}