package com.lonelys.Dubb.controller;

import com.lonelys.Dubb.dto.ClipDto;
import com.lonelys.Dubb.dto.DtoMapper;
import com.lonelys.Dubb.entity.Clip;
import com.lonelys.Dubb.service.ClipService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/clips")
public class ClipController {
    private final ClipService clipService;

    public ClipController(ClipService clipService) {
        this.clipService = clipService;
    }

    @GetMapping
    public List<ClipDto> getClips() {
        List<Clip> clips = clipService.getClips();
        List<ClipDto> result = new ArrayList<>();

        for (Clip clip : clips) {
            result.add(DtoMapper.toDto(clip));
        }

        return result;
    }
}
