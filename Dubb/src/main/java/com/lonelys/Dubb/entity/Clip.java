package com.lonelys.Dubb.entity;

import jakarta.persistence.*;

@Entity
public class Clip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Clip_ID;

    private String Clip_Title;
    private double Clip_Duration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Movie_ID")
    private Movie Source_Movie;

    public Clip() { }

    public Clip(String Clip_Title, double Clip_Duration, Movie source) {
        this.Clip_Title = Clip_Title;
        this.Clip_Duration = Clip_Duration;
        this.Source_Movie = source;
    }

    public long getClip_ID() {
        return Clip_ID;
    }

    public void setClip_ID(long clip_ID) {
        Clip_ID = clip_ID;
    }

    public String getClip_Title() {
        return Clip_Title;
    }

    public void setClip_Title(String clip_Title) {
        Clip_Title = clip_Title;
    }

    public double getClip_Duration() {
        return Clip_Duration;
    }

    public void setClip_Duration(double clip_Duration) {
        Clip_Duration = clip_Duration;
    }

    public Movie getSource_Movie() {
        return Source_Movie;
    }

    public void setSource_Movie(Movie source) {
        Source_Movie = source;
    }

    @Override
    public String toString() {
        return "\"" + Clip_Title + "\" from the movie: " + Source_Movie.getMovie_Title();
    }
}
