package com.lonelys.Dubb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Movie {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieID;

    private String movieTitle;
    private int movieYear;
    private String movieDirector;

    // OneToMany towards Movie
    @OneToMany(mappedBy = "sourceMovie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Clip> movieClips = new ArrayList<>();


    // Constructors
    public Movie() { }

    public Movie(String movieTitle, int movieYear, String movieDirector) {
        this.movieTitle = movieTitle;
        this.movieYear = movieYear;
        this.movieDirector = movieDirector;
    }


    // Getters
    public long getMovieID() {
        return movieID;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public int getMovieYear() {
        return movieYear;
    }

    public String getMovieDirector() {
        return movieDirector;
    }

    public List<Clip> getMovieClips() {
        return movieClips;
    }


    // Setters
    public void setMovieID(long movieID) {
        this.movieID = movieID;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public void setMovieYear(int movieYear) {
        this.movieYear = movieYear;
    }

    public void setMovieDirector(String movieDirector) {
        this.movieDirector = movieDirector;
    }

    public void setMovieClips(List<Clip> movieClips) {
        this.movieClips = movieClips;
    }


    // Methods
    @Override
    public String toString() {
        return "\"" + movieTitle + "\"" + ", directed by " + movieDirector + " in " + movieYear + ".";
    }

}
