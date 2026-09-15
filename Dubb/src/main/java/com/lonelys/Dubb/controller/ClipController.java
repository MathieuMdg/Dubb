package com.lonelys.Dubb.controller;

import com.lonelys.Dubb.dto.ClipDTO;
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
    public Iterable<ClipDTO> getClips(){
        Iterable<Clip> clips = clipService.getClips();
        List<ClipDTO> result = new ArrayList<>();

        for (Clip clip : clips){
            result.add(new ClipDTO(clip));
        }

        return result;
    }
}
