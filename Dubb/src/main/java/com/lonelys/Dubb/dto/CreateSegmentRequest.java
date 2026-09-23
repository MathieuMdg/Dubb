package com.lonelys.Dubb.dto;

public class CreateSegmentRequest {

    private double startTime;
    private double endTime;
    private int orderIndex;
    private boolean dubbable;
    private String label;

    public CreateSegmentRequest() {
    }

    public CreateSegmentRequest(double startTime, double endTime, int orderIndex,
                                boolean dubbable, String label) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.orderIndex = orderIndex;
        this.dubbable = dubbable;
        this.label = label;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}