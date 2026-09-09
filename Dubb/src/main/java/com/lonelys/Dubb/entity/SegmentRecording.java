package com.lonelys.Dubb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class SegmentRecording {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordingID;

    private String audioFilePath;
    private Double score;

    // ManyToOne towards Attempt
    @ManyToOne
    @JoinColumn(name = "attempt_id")
    @JsonIgnore
    private Attempt attempt;

    // ManyToOne towards Segment
    @ManyToOne
    @JoinColumn(name = "segment_id")
    private Segment segment;

    // Constructors
    public SegmentRecording() {}

    // Getters
    public Attempt getAttempt() {
        return attempt;
    }

    public Double getScore() {
        return score;
    }

    public Long getRecordingID() {
        return recordingID;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public Segment getSegment() {
        return segment;
    }


    // Setters
    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public void setRecordingID(Long recordingID) {
        this.recordingID = recordingID;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setAttempt(Attempt attempt) {
        this.attempt = attempt;
    }
}
