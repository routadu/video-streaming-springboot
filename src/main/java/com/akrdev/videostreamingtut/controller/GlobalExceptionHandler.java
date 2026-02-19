package com.akrdev.videostreamingtut.controller;

import com.akrdev.videostreamingtut.dto.error.ErrorResponseDto;
import com.akrdev.videostreamingtut.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomAccessDeniedException(CustomAccessDeniedException ex) {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .error(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(dto);
    }

    @ExceptionHandler({VideoNotFoundException.class, VideoFileNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(RuntimeException ex) {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .error(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(dto);
    }

    @ExceptionHandler(VideoUploadException.class)
    public ResponseEntity<ErrorResponseDto> handleVideoUploadException(VideoUploadException ex) {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .error(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(dto);
    }

    @ExceptionHandler(VideoFileNotAccessibleException.class)
    public ResponseEntity<ErrorResponseDto> handleVideoFileNotAccessibleException(VideoFileNotAccessibleException ex) {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .error(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(dto);
    }
}
