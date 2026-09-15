package com.lonelys.Dubb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Attempt {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptID;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String status;
    private String finalVideoPath;
    private Double globalScore;

    // ManyToOne towards User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // ManyToOne towards Clip
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clip_id")
    private Clip clip;

    // OneToMany towards SegmentRecording
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SegmentRecording> recordings = new ArrayList<>();


    // Constructors
    public Attempt() {
    }

    public Attempt(User user, Clip clip, String status, LocalDateTime startedAt) {
        this.user = user;
        this.clip = clip;
        this.status = status;
        this.startedAt = startedAt;
    }


    // Getters
    public Long getAttemptID() {
        return attemptID;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public List<SegmentRecording> getRecordings() {
        return recordings;
    }

    public String getFinalVideoPath() {
        return finalVideoPath;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public Double getGlobalScore() {
        return globalScore;
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

    public void setUser(User user) {
        this.user = user;
    }

    public void setClip(Clip clip) {
        this.clip = clip;
    }

    public void setRecordings(List<SegmentRecording> recordings) {
        this.recordings = recordings;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFinalVideoPath(String finalVideoPath) {
        this.finalVideoPath = finalVideoPath;
    }

    public void setGlobalScore(Double globalScore) {
        this.globalScore = globalScore;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    // Methods
    @Override
    public String toString() {
        return "Attempt[" + attemptID + "] score=" + globalScore;
    }

}