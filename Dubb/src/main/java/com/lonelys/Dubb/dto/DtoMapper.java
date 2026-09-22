package com.lonelys.Dubb.dto;

import com.lonelys.Dubb.entity.*;

import java.util.List;
import java.util.stream.Collectors;

public class DtoMapper {

    public static UserDto toDto(User user) {
        return new UserDto(user.getUserID(), user.getUsername(), user.getUserMail(), user.getUserJoindate());
    }

    public static MovieDto toDto(Movie movie) {
        return new MovieDto(movie.getMovieID(), movie.getMovieTitle(), movie.getMovieYear(), movie.getMovieDirector());
    }

    public static SegmentDto toDto(Segment segment) {
        return new SegmentDto(segment.getSegmentID(), segment.getStartTime(), segment.getEndTime(),
                segment.getOrderIndex(), segment.isDubbable());
    }

    public static ClipDto toDto(Clip clip) {
        List<SegmentDto> segmentDtos = clip.getSegments().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
        return new ClipDto(clip.getClipID(), clip.getClipTitle(), clip.getClipDuration(),
                clip.getSourceMovie().getMovieID(), segmentDtos);
    }

    public static SegmentRecordingDto toDto(SegmentRecording recording) {
        return new SegmentRecordingDto(recording.getRecordingID(), recording.getSegment().getSegmentID(), recording.getScore());
    }

    public static AttemptDto toDto(Attempt attempt) {
        List<SegmentRecordingDto> recordingDtos = attempt.getRecordings().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
        return new AttemptDto(attempt.getAttemptID(), attempt.getUser().getUserID(), attempt.getClip().getClipID(),
                attempt.getStatus(), attempt.getStartedAt(), attempt.getCompletedAt(),
                attempt.getGlobalScore(), recordingDtos);
    }
}