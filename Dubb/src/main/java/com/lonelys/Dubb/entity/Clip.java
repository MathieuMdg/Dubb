package com.lonelys.Dubb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Clip {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clipID;

    private String clipTitle;
    private double clipDuration; // in seconds

    // ManyToOne towards Movie
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    @JsonIgnore
    private Movie sourceMovie;

    // OneToMany towards Clip
    @OneToMany(mappedBy = "clip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Attempt> attempts = new ArrayList<>();

    // OneToMany towards Segment
    @OneToMany(mappedBy = "clip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Segment> segments = new ArrayList<>();


    // Constructors
    public Clip() { }

    public Clip(String Clip_Title, double Clip_Duration, Movie source) {
        this.clipTitle = Clip_Title;
        this.clipDuration = Clip_Duration;
        this.sourceMovie = source;
    }

    // Getters
    public long getClipID() {
        return clipID;
    }

    public String getClipTitle() {
        return clipTitle;
    }

    public double getClipDuration() {
        return clipDuration;
    }

    public Movie getSourceMovie() {
        return sourceMovie;
    }

    public List<Attempt> getAttempts() {
        return attempts;
    }

    public List<Segment> getSegments() {return segments;}


    // Setters
    public void setClipID(long clipID) {
        this.clipID = clipID;
    }

    public void setClipTitle(String clipTitle) {
        this.clipTitle = clipTitle;
    }

    public void setClipDuration(double clipDuration) {
        this.clipDuration = clipDuration;
    }

    public void setSourceMovie(Movie source) {
        sourceMovie = source;
    }

    public void setAttempts(List<Attempt> attempts) {
        this.attempts = attempts;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    // Methods
    @Override
    public String toString() {
        return "\"" + clipTitle + "\" from the movie: " + sourceMovie.getMovieTitle();
    }
}
