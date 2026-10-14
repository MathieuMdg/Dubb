package com.lonelys.Dubb.service;

import com.lonelys.Dubb.exception.AudioSeparationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DemucsService {

    private static final Logger log = LoggerFactory.getLogger(DemucsService.class);

    @Value("${app.storage.root}")
    private String storageRoot;

    @Value("${app.demucs.python:python}")
    private String pythonCommand;

    /** Returns the relative path of the no_vocals.wav file. */
    public String removeVocals(String audioRelativePath, String outputRelativeDir) {
        Path input = Paths.get(storageRoot, audioRelativePath).toAbsolutePath();
        Path outDir = Paths.get(storageRoot, outputRelativeDir).toAbsolutePath();

        List<String> command = List.of(
                pythonCommand, "-m", "demucs",
                "--two-stems=vocals",
                "-o", outDir.toString(),
                input.toString());

        try {
            log.info("Running demucs: {}", String.join(" ", command));
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            String output = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("demucs failed ({}): {}", exitCode, output);
                throw new AudioSeparationException("Voice removal failed with exit code " + exitCode);
            }
        } catch (IOException e) {
            throw new AudioSeparationException("Cannot start demucs: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AudioSeparationException("Voice removal was interrupted");
        }

        String fileName = input.getFileName().toString();
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        String result = outputRelativeDir + "/htdemucs/" + baseName + "/no_vocals.wav";

        if (!Files.exists(Paths.get(storageRoot, result))) {
            throw new AudioSeparationException("Demucs finished but no_vocals.wav was not found");
        }
        return result;
    }
}