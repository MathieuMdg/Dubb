package com.lonelys.Dubb.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Clip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long clipID;

    private String clipTitle;
    private double clipDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sourceMovie")
    private Movie sourceMovie;

    @OneToMany(mappedBy = "clip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AttemptJPA> attempts = new ArrayList<>();
    public Clip() { }

    public Clip(String Clip_Title, double Clip_Duration, Movie source) {
        this.clipTitle = Clip_Title;
        this.clipDuration = Clip_Duration;
        this.sourceMovie = source;
    }

    public long getClipID() {
        return clipID;
    }

    public void setClipID(long clipID) {
        this.clipID = clipID;
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

    public Movie getSourceMovie() {
        return sourceMovie;
    }

    public void setSourceMovie(Movie source) {
        sourceMovie = source;
    }

    public List<AttemptJPA> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<AttemptJPA> attempts) {
        this.attempts = attempts;
    }

    @Override
    public String toString() {
        return "\"" + clipTitle + "\" from the movie: " + sourceMovie.getMovieTitle();
    }
}
