package com.lonelys.Dubb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Attempt {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptID;

    private Double attemptScore;
    private LocalDateTime attemptDate;

    // ManyToOne towards User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // ManyToOne towards Clip
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clip_id")
    private Clip clip;


    // Constructors
    public Attempt() {
    }

    public Attempt(Double attemptScore, LocalDateTime attemptDate, User user) {
        this.attemptScore = attemptScore;
        this.attemptDate = attemptDate;
        this.user = user;
    }


    // Getters
    public Long getAttemptID() {
        return attemptID;
    }

    public Double getAttemptScore() {
        return attemptScore;
    }

    public LocalDateTime getAttemptDate() {
        return attemptDate;
    }

    public User getUser() {
        return user;
    }

    public Clip getClip() {
        return clip;
    }


    // Setters
    public void setAttemptID(Long attemptId) {
        this.attemptID = attemptId;
    }

    public void setAttemptScore(Double score) {
        this.attemptScore = score;
    }

    public void setAttemptDate(LocalDateTime attemptDate) {
        this.attemptDate = attemptDate;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setClip(Clip clip) {
        this.clip = clip;
    }


    // Methods
    @Override
    public String toString() {
        return "Attempt[" + attemptID + "] score=" + attemptScore;
    }

}