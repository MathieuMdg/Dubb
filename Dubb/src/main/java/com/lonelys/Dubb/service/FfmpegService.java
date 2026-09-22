package com.lonelys.Dubb.service;

import com.lonelys.Dubb.entity.Attempt;
import com.lonelys.Dubb.entity.Segment;
import com.lonelys.Dubb.entity.SegmentRecording;
import com.lonelys.Dubb.exception.FfmpegProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lonelys.Dubb.repository.AttemptRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FfmpegService {

    private static final Logger log = LoggerFactory.getLogger(FfmpegService.class);

    @Value("${app.storage.root}")
    private String storageRoot;

    private final AttemptRepository attemptRepository;

    public FfmpegService(AttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    //Creates the final audio track for an attempt.
    @Transactional
    public String createFinalAudio(Long attemptId) {

        Attempt attempt = attemptRepository.findById(attemptId).orElseThrow(() -> new IllegalArgumentException("Attempt not found: " + attemptId));
        List<Segment> segments = attempt.getClip().getSegments().stream().sorted(Comparator.comparingInt(Segment::getOrderIndex)).collect(Collectors.toList());
        Map<Long, SegmentRecording> recordingsBySegmentId = attempt.getRecordings().stream().collect(Collectors.toMap(r -> r.getSegment().getSegmentID(), r -> r));

        String originalVideoPath = resolveAbsolutePath(attempt.getClip().getVideoFilePath());

        String workingDir = "attempts/" + attempt.getAttemptID() + "/audio";
        createWorkingDirectory(workingDir);

        List<String> audioChunkPaths = new ArrayList<>();
        double cursor = 0.0;

        for (Segment segment : segments) {

            if (!segment.isDubbable()) {
                continue;
            }

            SegmentRecording recording = recordingsBySegmentId.get(segment.getSegmentID());

            if (recording == null) {
                throw new FfmpegProcessingException("Missing recording for dubbable segment " + segment.getSegmentID()
                );
            }

            double start = segment.getStartTime();
            double end = segment.getEndTime();

            if (end <= start) {
                throw new FfmpegProcessingException("Invalid segment duration for segment " + segment.getSegmentID());
            }

            if (start > cursor) {

                String originalChunkRelativePath = workingDir + "/original_" + UUID.randomUUID() + ".wav";
                String originalChunkAbsolutePath = resolveAbsolutePath(originalChunkRelativePath);
                extractAudioSegment(originalVideoPath, cursor, start, originalChunkAbsolutePath);

                audioChunkPaths.add(originalChunkAbsolutePath);
            }

            String recordingAudioPath = resolveAbsolutePath(recording.getAudioFilePath());
            String recordingChunkRelativePath = workingDir + "/recording_" + segment.getOrderIndex() + "_" + UUID.randomUUID() + ".wav";
            String recordingChunkAbsolutePath = resolveAbsolutePath(recordingChunkRelativePath);

            normalizeRecording(recordingAudioPath, recordingChunkAbsolutePath, end - start);

            audioChunkPaths.add(recordingChunkAbsolutePath);
            cursor = end;
        }

        String durationFileRelativePath = workingDir + "/original_tail_" + UUID.randomUUID() + ".wav";
        String durationFileAbsolutePath = resolveAbsolutePath(durationFileRelativePath);

        if (cursor > 0) {
            extractAudioFrom(originalVideoPath, cursor, durationFileAbsolutePath);
            audioChunkPaths.add(durationFileAbsolutePath);

        } else {

            extractFullAudio(originalVideoPath, durationFileAbsolutePath);
            audioChunkPaths.add(durationFileAbsolutePath);
        }

        String finalRelativePath = "attempts/" + attempt.getAttemptID() + "/final_" + UUID.randomUUID() + ".wav";
        String finalAbsolutePath = resolveAbsolutePath(finalRelativePath);

        concatenateAudio(audioChunkPaths, finalAbsolutePath);
        cleanupWorkingDirectory(workingDir);

        log.info("Final audio assembled for attempt {} at {}", attempt.getAttemptID(), finalRelativePath);

        return finalRelativePath;
    }


    //Extracts a specific part of the original audio.
    private void extractAudioSegment(String sourceVideoPath, double start, double end, String outputPath) {

        double duration = end - start;
        List<String> command = List.of("ffmpeg", "-y", "-i", sourceVideoPath, "-ss", String.valueOf(start), "-t", String.valueOf(duration), "-vn", "-acodec", "pcm_s16le", outputPath);

        runCommand(command, "extractAudioSegment");
    }

    //Extracts the original audio from a given position until the end.
    private void extractAudioFrom(String sourceVideoPath, double start, String outputPath) {

        List<String> command = List.of(
                "ffmpeg",
                "-y",
                "-ss",
                String.valueOf(start),
                "-i",
                sourceVideoPath,
                "-vn",
                "-acodec",
                "pcm_s16le",
                outputPath
        );

        runCommand(command, "extractAudioFrom");
    }

    private void extractFullAudio(
            String sourceVideoPath,
            String outputPath) {

        List<String> command = List.of(
                "ffmpeg",
                "-y",
                "-i",
                sourceVideoPath,
                "-vn",
                "-acodec",
                "pcm_s16le",
                outputPath
        );

        runCommand(command, "extractFullAudio");
    }

    private void normalizeRecording(
            String inputPath,
            String outputPath,
            double expectedDuration) {

        List<String> command = List.of(
                "ffmpeg",
                "-y",
                "-i",
                inputPath,
                "-t",
                String.valueOf(expectedDuration),
                "-ar",
                "48000",
                "-ac",
                "2",
                "-acodec",
                "pcm_s16le",
                outputPath
        );

        runCommand(command, "normalizeRecording");
    }

    private void concatenateAudio(
            List<String> audioChunkPaths,
            String outputPath) {

        Path listFile = null;

        try {

            listFile = Files.createTempFile(
                    "audio_concat_list_",
                    ".txt"
            );

            List<String> lines = audioChunkPaths.stream()
                    .map(path ->
                            "file '"
                                    + path.replace("'", "'\\''")
                                    + "'"
                    )
                    .collect(Collectors.toList());

            Files.write(listFile, lines);

            List<String> command = List.of(
                    "ffmpeg",
                    "-y",
                    "-f",
                    "concat",
                    "-safe",
                    "0",
                    "-i",
                    listFile.toAbsolutePath().toString(),
                    "-acodec",
                    "pcm_s16le",
                    outputPath
            );

            runCommand(command, "concatenateAudio");

        } catch (IOException e) {

            log.error(
                    "Failed to create audio concat list file",
                    e
            );

            throw new FfmpegProcessingException(
                    "Failed to prepare audio concatenation: "
                            + e.getMessage()
            );

        } finally {

            if (listFile != null) {
                try {
                    Files.deleteIfExists(listFile);
                } catch (IOException e) {
                    log.warn(
                            "Failed to delete temporary concat list file",
                            e
                    );
                }
            }
        }
    }

    private void runCommand(
            List<String> command,
            String stepName) {

        try {

            log.info(
                    "Running ffmpeg step [{}]: {}",
                    stepName,
                    String.join(" ", command)
            );

            ProcessBuilder processBuilder =
                    new ProcessBuilder(command);

            processBuilder.redirectErrorStream(true);

            Process process =
                    processBuilder.start();

            String output =
                    new String(
                            process.getInputStream().readAllBytes()
                    );

            int exitCode =
                    process.waitFor();

            if (exitCode != 0) {

                log.error(
                        "ffmpeg step [{}] failed with exit code {}. Output:\n{}",
                        stepName,
                        exitCode,
                        output
                );

                throw new FfmpegProcessingException(
                        "ffmpeg step '"
                                + stepName
                                + "' failed with exit code "
                                + exitCode
                );
            }

            log.debug(
                    "ffmpeg step [{}] completed successfully",
                    stepName
            );

        } catch (IOException e) {

            log.error(
                    "ffmpeg step [{}] failed to start",
                    stepName,
                    e
            );

            throw new FfmpegProcessingException(
                    "Failed to run ffmpeg step '"
                            + stepName
                            + "': "
                            + e.getMessage()
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            log.error(
                    "ffmpeg step [{}] was interrupted",
                    stepName,
                    e
            );

            throw new FfmpegProcessingException(
                    "ffmpeg step '"
                            + stepName
                            + "' was interrupted"
            );
        }
    }

    private void createWorkingDirectory(
            String relativePath) {

        try {

            Files.createDirectories(
                    Paths.get(storageRoot, relativePath)
            );

        } catch (IOException e) {

            log.error(
                    "Failed to create working directory {}",
                    relativePath,
                    e
            );

            throw new FfmpegProcessingException(
                    "Failed to create working directory: "
                            + e.getMessage()
            );
        }
    }

    private void cleanupWorkingDirectory(
            String relativePath) {

        try {

            Path dir =
                    Paths.get(storageRoot, relativePath);

            if (Files.exists(dir)) {

                Files.walk(dir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {

                            try {

                                Files.deleteIfExists(path);

                            } catch (IOException e) {

                                log.warn(
                                        "Failed to delete temporary file {}",
                                        path,
                                        e
                                );
                            }
                        });
            }

        } catch (IOException e) {

            log.warn(
                    "Failed to clean up working directory {}",
                    relativePath,
                    e
            );
        }
    }

    private String resolveAbsolutePath(
            String relativePath) {

        return Paths.get(
                storageRoot,
                relativePath
        ).toAbsolutePath().toString();
    }

    @Transactional
    public String exportFinalVideo(Long attemptId, String finalAudioPath) {

        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Attempt not found: " + attemptId
                        ));

        String originalVideoPath =
                resolveAbsolutePath(
                        attempt.getClip().getVideoFilePath()
                );

        String finalAudioAbsolutePath =
                resolveAbsolutePath(finalAudioPath);

        String finalRelativePath =
                "attempts/"
                        + attempt.getAttemptID()
                        + "/final_"
                        + UUID.randomUUID()
                        + ".mp4";

        String finalAbsolutePath =
                resolveAbsolutePath(finalRelativePath);

        List<String> command = List.of(
                "ffmpeg",
                "-y",
                "-i",
                originalVideoPath,
                "-i",
                finalAudioAbsolutePath,
                "-map",
                "0:v:0",
                "-map",
                "1:a:0",
                "-c:v",
                "copy",
                "-c:a",
                "aac",
                "-shortest",
                finalAbsolutePath
        );

        runCommand(command, "exportFinalVideo");

        log.info(
                "Final video exported for attempt {} at {}",
                attempt.getAttemptID(),
                finalRelativePath
        );

        return finalRelativePath;
    }
}
