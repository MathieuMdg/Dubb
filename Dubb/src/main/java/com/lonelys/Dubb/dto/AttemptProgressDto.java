package com.lonelys.Dubb.dto;

import com.lonelys.Dubb.entity.Segment;

import java.util.List;

public class AttemptProgressDto {

    private final int totalSegmentsDubbables;
    private final int segmentsEnregistres;
    private final List<Segment> segmentsManquants;
    private final boolean complete;

    public AttemptProgressDto(int totalSegmentsDubbables, int segmentsEnregistres, List<Segment> segmentsManquants, boolean complete) {
        this.totalSegmentsDubbables = totalSegmentsDubbables;
        this.segmentsEnregistres = segmentsEnregistres;
        this.segmentsManquants = segmentsManquants;
        this.complete = complete;
    }

    public int getTotalSegmentsDubbables() {
        return totalSegmentsDubbables;
    }

    public int getSegmentsEnregistres() {
        return segmentsEnregistres;
    }

    public List<Segment> getSegmentsManquants() {
        return segmentsManquants;
    }

    public boolean isComplete() {
        return complete;
    }
}