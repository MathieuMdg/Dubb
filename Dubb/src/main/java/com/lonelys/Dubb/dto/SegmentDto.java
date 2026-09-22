package com.lonelys.Dubb.dto;

public class SegmentDto {

    private Long segmentId;
    private double startTime;
    private double endTime;
    private int orderIndex;
    private boolean dubbable;
    private String character;

    public SegmentDto() {
    }

    public SegmentDto(Long segmentId, double startTime, double endTime,
                      int orderIndex, boolean dubbable) {
        this.segmentId = segmentId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.orderIndex = orderIndex;
        this.dubbable = dubbable;
        this.character = character;
    }

    public Long getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(Long segmentId) {
        this.segmentId = segmentId;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public boolean isDubbable() {
        return dubbable;
    }

    public void setDubbable(boolean dubbable) {
        this.dubbable = dubbable;
    }

    public void setCharacter(String character) {
        this.character = character;
    }
}