package com.lonelys.Dubb.dto;

import java.util.List;

public class AttemptProgressDto {

    private int totalDubbableSegments;
    private int recordedSegments;
    private List<Long> missingSegmentIds;
    private boolean complete;

    public AttemptProgressDto() {
    }

    public AttemptProgressDto(int totalDubbableSegments, int recordedSegments,
                              List<Long> missingSegmentIds, boolean complete) {
        this.totalDubbableSegments = totalDubbableSegments;
        this.recordedSegments = recordedSegments;
        this.missingSegmentIds = missingSegmentIds;
        this.complete = complete;
    }

    public int getTotalDubbableSegments() {
        return totalDubbableSegments;
    }

    public void setTotalDubbableSegments(int totalDubbableSegments) {
        this.totalDubbableSegments = totalDubbableSegments;
    }

    public int getRecordedSegments() {
        return recordedSegments;
    }

    public void setRecordedSegments(int recordedSegments) {
        this.recordedSegments = recordedSegments;
    }

    public List<Long> getMissingSegmentIds() {
        return missingSegmentIds;
    }

    public void setMissingSegmentIds(List<Long> missingSegmentIds) {
        this.missingSegmentIds = missingSegmentIds;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}