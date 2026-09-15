package com.lonelys.Dubb.dto;

import com.lonelys.Dubb.entity.Clip;

public class ClipDTO {

    private Long clipID;
    private String clipTitle;
    private double clipDuration;
    private String movieTitle;

    public ClipDTO(Clip clip) {
        this.clipID = clip.getClipID();
        this.clipTitle = clip.getClipTitle();
        this.clipDuration = clip.getClipDuration();
        this.movieTitle = clip.getSourceMovie().getMovieTitle();
    }

    public Long getClipID() {
        return clipID;
    }

    public String getClipTitle() {
        return clipTitle;
    }

    public double getClipDuration() {
        return clipDuration;
    }

    public String getMovieTitle() {
        return movieTitle;
    }
}
