package com.lonelys.Dubb.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long movieID;

    private String movieTitle;
    private int movieYear;
    private String movieDirector;

    @OneToMany(mappedBy = "Movie_ID", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Clip> movieClips = new ArrayList<>();

    public Movie() { }

    public Movie(String Movie_Title, int Movie_Year, String Movie_Director) {
        this.movieTitle = Movie_Title;
        this.movieYear = Movie_Year;
        this.movieDirector = Movie_Director;
    }

    public long getMovieID() {
        return movieID;
    }

    public void setMovieID(long Movie_ID) {
        this.movieID = Movie_ID;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String Movie_Title) {
        this.movieTitle = Movie_Title;
    }

    public int getMovieYear() {
        return movieYear;
    }

    public void setMovieYear(int Movie_Year) {
        this.movieYear = Movie_Year;
    }

    public String getMovieDirector() {
        return movieDirector;
    }

    public void setMovieDirector(String Movie_Director) {
        this.movieDirector = Movie_Director;
    }

    public List<Clip> getMovieClips() {
        return movieClips;
    }

    public void setMovieClips(List<Clip> Movie_Clips) {
        this.movieClips = Movie_Clips;
    }

    @Override
    public String toString() {
        return "\"" + movieTitle + "\"" + ", directed by " + movieDirector + " in " + movieYear + ".";
    }

}
