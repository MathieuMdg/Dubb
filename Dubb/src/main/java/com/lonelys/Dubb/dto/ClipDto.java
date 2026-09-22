package com.lonelys.Dubb.dto;

import java.util.List;

public class ClipDto {

    private Long clipId;
    private String clipTitle;
    private double clipDuration;
    private Long movieId;
    private List<SegmentDto> segments;

    public ClipDto() {
    }

    public ClipDto(Long clipId, String clipTitle, double clipDuration,
                   Long movieId, List<SegmentDto> segments) {
        this.clipId = clipId;
        this.clipTitle = clipTitle;
        this.clipDuration = clipDuration;
        this.movieId = movieId;
        this.segments = segments;
    }

    public Long getClipId() {
        return clipId;
    }

    public void setClipId(Long clipId) {
        this.clipId = clipId;
    }

    public String getClipTitle() {
        return clipTitle;
    }

    public void setClipTitle(String clipTitle) {
        this.clipTitle = clipTitle;
    }

    public double getClipDuration() {
        return clipDuration;
    }

    public void setClipDuration(double clipDuration) {
        this.clipDuration = clipDuration;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public List<SegmentDto> getSegments() {
        return segments;
    }

    public void setSegments(List<SegmentDto> segments) {
        this.segments = segments;
    }
}