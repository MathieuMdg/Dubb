package com.lonelys.Dubb.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Movie_ID;

    private String Movie_Title;
    private int Movie_Year;
    private String Movie_Director;

    @OneToMany(mappedBy = "Movie_ID", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Clip> Movie_Clips = new ArrayList<>();

    public Movie() { }

    public Movie(String Movie_Title, int Movie_Year, String Movie_Director) {
        this.Movie_Title = Movie_Title;
        this.Movie_Year = Movie_Year;
        this.Movie_Director = Movie_Director;
    }

    public long getMovie_ID() {
        return Movie_ID;
    }

    public void setMovie_ID(long Movie_ID) {
        this.Movie_ID = Movie_ID;
    }

    public String getMovie_Title() {
        return Movie_Title;
    }

    public void setMovie_Title(String Movie_Title) {
        this.Movie_Title = Movie_Title;
    }

    public int getMovie_Year() {
        return Movie_Year;
    }

    public void setMovie_Year(int Movie_Year) {
        this.Movie_Year = Movie_Year;
    }

    public String getMovie_Director() {
        return Movie_Director;
    }

    public void setMovie_Director(String Movie_Director) {
        this.Movie_Director = Movie_Director;
    }

    public List<Clip> getMovie_Clips() {
        return Movie_Clips;
    }

    public void setMovie_Clips(List<Clip> Movie_Clips) {
        this.Movie_Clips = Movie_Clips;
    }

    @Override
    public String toString() {
        return "\"" + Movie_Title + "\"" + ", directed by " + Movie_Director + " in " + Movie_Year + ".";
    }

}
