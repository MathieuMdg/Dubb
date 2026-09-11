package com.lonelys.Dubb.service;

import com.lonelys.Dubb.entity.Clip;
import com.lonelys.Dubb.repository.ClipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    public List<Clip> getClips(){
        return (List<Clip>) clipRepository.findAll();
    }



}
