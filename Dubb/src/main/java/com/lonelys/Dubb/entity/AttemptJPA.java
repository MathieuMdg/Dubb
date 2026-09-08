package com.lonelys.Dubb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AttemptJPA {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clip")
    private Clip clip;


    // Constructors
    public AttemptJPA() {
    }

    public AttemptJPA(Double attemptScore, LocalDateTime attemptDate, User user) {
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

    public void setAttemptDate(LocalDateTime recordedAt) {
        this.attemptDate = recordedAt;
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