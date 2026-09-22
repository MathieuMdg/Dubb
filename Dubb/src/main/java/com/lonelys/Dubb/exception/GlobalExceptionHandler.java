package com.lonelys.Dubb.exception;

import com.lonelys.Dubb.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 - Not found
    @ExceptionHandler({
            UserNotFoundException.class,
            MovieNotFoundException.class,
            ClipNotFoundException.class,
            FileNotFoundStorageException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 400 - Invalid data / bad request
    @ExceptionHandler({
            InvalidUserDataException.class,
            DuplicateException.class,
            InvalidSegmentException.class,
            InvalidSegmentRecordingException.class,
            InvalidUserException.class,
            InvalidClipException.class,
            InvalidAttemptException.class,
            InvalidMovieException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {
        log.warn("Invalid request: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 500 - Technical/server errors
    @ExceptionHandler({
            FileStorageException.class,
            FfmpegProcessingException.class
    })
    public ResponseEntity<ErrorResponse> handleServerError(RuntimeException ex) {
        log.error("Server error: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // Catch-all fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(status.value(), status.getReasonPhrase(), message);
        return ResponseEntity.status(status).body(error);
    }
}