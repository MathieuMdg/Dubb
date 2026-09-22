package com.lonelys.Dubb.controller;

import com.lonelys.Dubb.dto.*;
import com.lonelys.Dubb.entity.Attempt;
import com.lonelys.Dubb.service.AttemptService;
import com.lonelys.Dubb.service.SegmentRecordingService;
import com.lonelys.Dubb.service.StorageService;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/attempts")
public class AttemptController {

    private final AttemptService attemptService;
    private final SegmentRecordingService segmentRecordingService;
    private final StorageService storageService;

    public AttemptController(AttemptService attemptService,
                             SegmentRecordingService segmentRecordingService,
                             StorageService storageService) {
        this.attemptService = attemptService;
        this.segmentRecordingService = segmentRecordingService;
        this.storageService = storageService;
    }

    @PostMapping
    public AttemptDto startAttempt(@RequestBody StartAttemptRequest request) {
        Attempt attempt = attemptService.startAttempt(request.getUserId(), request.getClipId());
        return DtoMapper.toDto(attempt);
    }

    @PostMapping("/{attemptId}/segments/{segmentId}")
    public SegmentRecordingDto recordSegment(@PathVariable Long attemptId,
                                             @PathVariable Long segmentId,
                                             @RequestParam MultipartFile audio) {
        var recording = segmentRecordingService.recordSegment(attemptId, segmentId, audio);
        return DtoMapper.toDto(recording);
    }

    @GetMapping("/{attemptId}/progress")
    public AttemptProgressDto getProgression(@PathVariable Long attemptId) {
        return attemptService.getProgression(attemptId);
    }

    @PostMapping("/{attemptId}/finalize")
    public AttemptDto finalizeAttempt(@PathVariable Long attemptId) {
        Attempt attempt = attemptService.finaliseAttempt(attemptId);
        return DtoMapper.toDto(attempt);
    }

    @GetMapping("/{attemptId}/final-video")
    public ResponseEntity<Resource> streamFinalVideo(@PathVariable Long attemptId) {
        File videoFile = storageService.get(attemptService.getFinalVideoPath(attemptId));
        Resource resource = new FileSystemResource(videoFile);
        return ResponseEntity.ok().contentType(MediaType.valueOf("video/mp4")).body(resource);
    }
}