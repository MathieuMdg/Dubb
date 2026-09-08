package com.lonelys.Dubb.entity;

import jakarta.persistence.*;

@Entity
public class Clip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long clipID;

    private String clipTitle;
    private double clipDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Movie_ID")
    private Movie sourceMovie;

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

    @Override
    public String toString() {
        return "\"" + clipTitle + "\" from the movie: " + sourceMovie.getMovieTitle();
    }
}
