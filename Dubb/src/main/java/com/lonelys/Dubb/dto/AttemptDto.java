package com.lonelys.Dubb.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AttemptDto {

    private Long attemptId;
    private Long userId;
    private Long clipId;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Double globalScore;
    private List<SegmentRecordingDto> recordings;

    public AttemptDto() {
    }

    public AttemptDto(Long attemptId, Long userId, Long clipId, String status,
                      LocalDateTime startedAt, LocalDateTime completedAt,
                      Double globalScore, List<SegmentRecordingDto> recordings) {
        this.attemptId = attemptId;
        this.userId = userId;
        this.clipId = clipId;
        this.status = status;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.globalScore = globalScore;
        this.recordings = recordings;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getClipId() {
        return clipId;
    }

    public void setClipId(Long clipId) {
        this.clipId = clipId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Double getGlobalScore() {
        return globalScore;
    }

    public void setGlobalScore(Double globalScore) {
        this.globalScore = globalScore;
    }

    public List<SegmentRecordingDto> getRecordings() {
        return recordings;
    }

    public void setRecordings(List<SegmentRecordingDto> recordings) {
        this.recordings = recordings;
    }
}