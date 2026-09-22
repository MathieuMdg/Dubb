package com.lonelys.Dubb.dto;

public class CreateMovieRequest {

    private String movieTitle;
    private int movieYear;
    private String movieDirector;

    public CreateMovieRequest() {
    }

    public CreateMovieRequest(String movieTitle, int movieYear, String movieDirector) {
        this.movieTitle = movieTitle;
        this.movieYear = movieYear;
        this.movieDirector = movieDirector;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getMovieYear() {
        return movieYear;
    }

    public void setMovieYear(int movieYear) {
        this.movieYear = movieYear;
    }

    public String getMovieDirector() {
        return movieDirector;
    }

    public void setMovieDirector(String movieDirector) {
        this.movieDirector = movieDirector;
    }
}