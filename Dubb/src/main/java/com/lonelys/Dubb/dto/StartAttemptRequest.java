package com.lonelys.Dubb.dto;

public class StartAttemptRequest {

    private Long userId;
    private Long clipId;

    public StartAttemptRequest() {
    }

    public StartAttemptRequest(Long userId, Long clipId) {
        this.userId = userId;
        this.clipId = clipId;
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
}