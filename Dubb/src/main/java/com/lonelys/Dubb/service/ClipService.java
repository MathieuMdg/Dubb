package com.lonelys.Dubb.service;

import com.lonelys.Dubb.entity.Clip;
import com.lonelys.Dubb.entity.Movie;
import com.lonelys.Dubb.exception.ClipNotFoundException;
import com.lonelys.Dubb.exception.InvalidClipException;
import com.lonelys.Dubb.repository.ClipRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ClipService {

    // Attributes
    // Specify UserService in logs
    private static final Logger log = LoggerFactory.getLogger(ClipService.class);

    // Service access UserRepository
    private final ClipRepository clipRepository;
    private final MovieService movieService;
    private final StorageService storageService;

    //Constructors
    public ClipService(ClipRepository clipRepository, MovieService movieService, StorageService storageService) {
        this.clipRepository = clipRepository;
        this.movieService = movieService;
        this.storageService = storageService;
    }

    //Methods
    public List<Clip> getClips() {
        return (List<Clip>) clipRepository.findAll();
    }

    public Clip createClip(Long movieId, String title, double duration, MultipartFile video){
        checkClipData(title, duration, video);
        Movie source = movieService.getMovieById(movieId);

        String videoPath = storageService.save(video, "clips/" + movieId);

        Clip clip = new Clip(title, duration, source);
        clip.setVideoFilePath(videoPath);

        Clip savedClip = clipRepository.save(clip);
        log.info("New Clip added : {}", savedClip);
        return savedClip;
    }

    public Clip getClipById(Long id){

        return clipRepository.findById(id).orElseThrow(() -> new ClipNotFoundException("Clip with id " + id + " not found"));
    }

    public List<Clip> listClipsByMovie(Long movieId){
        return (List<Clip>) clipRepository.findBySourceMovie(movieService.getMovieById(movieId));
    }

    private void checkClipData(String title, double duration, MultipartFile video) {
        if (title == null || title.isBlank()) {
            throw new InvalidClipException("Title must not be blank");
        }
        if (duration <= 0) {
            throw new InvalidClipException("Duration must be positive");
        }
        if (video == null || video.isEmpty()) {
            throw new InvalidClipException("Video file must not be empty");
        }
    }


}
