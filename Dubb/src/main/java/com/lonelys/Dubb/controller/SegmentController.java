package com.lonelys.Dubb.controller;

import com.lonelys.Dubb.dto.CreateSegmentRequest;
import com.lonelys.Dubb.dto.DtoMapper;
import com.lonelys.Dubb.dto.SegmentDto;
import com.lonelys.Dubb.entity.Clip;
import com.lonelys.Dubb.entity.Segment;
import com.lonelys.Dubb.service.ClipService;
import com.lonelys.Dubb.service.SegmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clips/{clipId}/segments")
public class SegmentController {

    private final SegmentService segmentService;
    private final ClipService clipService;

    public SegmentController(SegmentService segmentService, ClipService clipService) {
        this.segmentService = segmentService;
        this.clipService = clipService;
    }

    @PostMapping
    public SegmentDto createSegment(@PathVariable Long clipId, @RequestBody CreateSegmentRequest request) {
        Clip clip = clipService.getClipById(clipId);
        Segment segment = segmentService.createSegment(clip, request.getStartTime(), request.getEndTime(),
                request.getOrderIndex(), request.isDubbable(), request.getLabel());
        return DtoMapper.toDto(segment);
    }

    @GetMapping
    public List<SegmentDto> getSegmentsByClip(@PathVariable Long clipId) {
        Clip clip = clipService.getClipById(clipId);
        return segmentService.getSegmentByClip(clip).stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }
}