package com.lonelys.Dubb.dto;

public class SegmentRecordingDto {

    private Long recordingId;
    private Long segmentId;
    private Double score;

    public SegmentRecordingDto() {
    }

    public SegmentRecordingDto(Long recordingId, Long segmentId, Double score) {
        this.recordingId = recordingId;
        this.segmentId = segmentId;
        this.score = score;
    }

    public Long getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(Long recordingId) {
        this.recordingId = recordingId;
    }

    public Long getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(Long segmentId) {
        this.segmentId = segmentId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}