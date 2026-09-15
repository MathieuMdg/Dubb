package com.lonelys.Dubb.service;

import com.lonelys.Dubb.exception.FileStorageException;
import com.lonelys.Dubb.exception.FileNotFoundStorageException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    @Value("${app.storage.root}")
    private String storageRoot;

    public String save(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            log.warn("Trying to save an empty or nonexistent file");
            throw new FileStorageException("File empty or nonexistent");
        }

        try {
            // Create directory
            Path targetFolder = Paths.get(storageRoot, subFolder);
            Files.createDirectories(targetFolder);

            // (Just for the test) Generate a random file name
            String extension = getExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension;

            // Copy the fil content
            Path absolutePath = targetFolder.resolve(fileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, absolutePath, StandardCopyOption.REPLACE_EXISTING);
            }

            String relativePath = subFolder + "/" + fileName;
            log.info("File saved : {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            log.error("Failed saving file in {}", subFolder, e);
            throw new FileStorageException("Failed to save the file : " + e.getMessage());
        }
    }

    public String save(File file, String subFolder) {
        if (file == null || !file.exists() || file.length() == 0) {
            log.warn("Trying to save an empty or nonexistent file");
            throw new FileStorageException("File empty or nonexistent");
        }

        try {
            Path targetPath = Paths.get(storageRoot, subFolder);
            Files.createDirectories(targetPath);

            String extension = getExtension(file.getName());

            String fileName = UUID.randomUUID() + extension;

            Path absolutePath = targetPath.resolve(fileName);

            Files.copy(
                    file.toPath(),
                    absolutePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String relativePath = subFolder + "/" + fileName;

            log.info("File saved : {}", relativePath);

            return relativePath;

        } catch (IOException e) {
            log.error("Failed saving file in {}", subFolder, e);
            throw new FileStorageException("Failed to save the file : " + e.getMessage());
        }
    }

    public File get(String relativePath) {
        if (!exists(relativePath)) {
            log.warn("No file found : {}", relativePath);
            throw new FileNotFoundStorageException("No file found : " + relativePath);
        }
        return getAbsolutePath(relativePath).toFile();
    }

    public void delete(String relativePath) {
        Path path = getAbsolutePath(relativePath);
        try {
            boolean delete = Files.deleteIfExists(path);
            if (delete) {
                log.info("File deleted : {}", relativePath);
            } else {
                log.warn("file already missing : {}", relativePath);
            }
        } catch (IOException e) {
            log.error("Can't delete the file {}", relativePath, e);
            throw new FileStorageException("Can't delete the file : " + e.getMessage());
        }
    }

    public boolean exists(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return false;
        }
        return Files.exists(getAbsolutePath(relativePath));
    }

    private Path getAbsolutePath(String relativePath) {
        return Paths.get(storageRoot, relativePath);
    }

    //Get the file .extension
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
