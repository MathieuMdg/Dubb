package com.lonelys.Dubb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Segment {

    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long segmentID;

    private double startTime;
    private double endTime;
    private int orderIndex;
    private boolean dubbable;

    // ManyToOne towards Clip
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clip_id")
    @JsonIgnore
    private Clip clip;

    // Constructors
    public Segment(){}

    public Segment(Clip clip, double startTime, double endTime, int orderIndex, boolean dubbable){
        this.clip = clip;
        this.dubbable = dubbable;
        this.endTime = endTime;
        this.orderIndex = orderIndex;
        this.startTime = startTime;
    }

    // Getters
    public Long getSegmentID() {
        return segmentID;
    }

    public double getEndTime() {
        return endTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public Clip getClip() {
        return clip;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public boolean isDubbable() {
        return dubbable;
    }


    // Setters
    public void setDubbable(boolean dubbable) {
        this.dubbable = dubbable;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public void setClip(Clip clip) {
        this.clip = clip;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public void setSegmentID(Long segmentID) {
        this.segmentID = segmentID;
    }


}
