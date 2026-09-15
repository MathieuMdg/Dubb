package com.lonelys.Dubb.service;

import com.lonelys.Dubb.entity.Clip;
import com.lonelys.Dubb.repository.ClipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClipService {

    // Attributes
    // Specify UserService in logs
    private static final Logger log = LoggerFactory.getLogger(ClipService.class);

    // Service access UserRepository
    private final ClipRepository clipRepository;

    //Constructors
    public ClipService(ClipRepository clipRepository){
        this.clipRepository = clipRepository;
    }

    //Methods
    public Iterable<Clip> getClips(){
        return clipRepository.findAll();
    }



}
