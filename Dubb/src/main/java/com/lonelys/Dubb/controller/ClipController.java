package com.lonelys.Dubb.controller;

import com.lonelys.Dubb.dto.ClipDto;
import com.lonelys.Dubb.dto.DtoMapper;
import com.lonelys.Dubb.entity.Clip;
import com.lonelys.Dubb.service.ClipService;
import com.lonelys.Dubb.service.StorageService;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clips")
public class ClipController {

    private final ClipService clipService;
    private final StorageService storageService;

    public ClipController(ClipService clipService, StorageService storageService) {
        this.clipService = clipService;
        this.storageService = storageService;
    }

    @PostMapping
    public ClipDto createClip(@RequestParam Long movieId,
                              @RequestParam String title,
                              @RequestParam double duration,
                              @RequestParam MultipartFile video) {
        Clip clip = clipService.createClip(movieId, title, duration, video);
        return DtoMapper.toDto(clip);
    }

    @GetMapping("/{id}")
    public ClipDto getClipById(@PathVariable Long id) {
        Clip clip = clipService.getClipById(id);
        return DtoMapper.toDto(clip);
    }

    @GetMapping
    public List<ClipDto> getClips() {
        return clipService.getClips().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(params = "movieId")
    public List<ClipDto> getClipsByMovie(@RequestParam Long movieId) {
        return clipService.listClipsByMovie(movieId).stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/video")
    public ResponseEntity<Resource> streamOriginalVideo(@PathVariable Long id) {
        Clip clip = clipService.getClipById(id);
        File videoFile = storageService.get(clip.getVideoFilePath());
        Resource resource = new FileSystemResource(videoFile);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("video/mp4"))
                .body(resource);
    }
}