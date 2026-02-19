package com.akrdev.videostreamingtut.exception;

import com.akrdev.videostreamingtut.entity.video.videofile.VideoFile;

import java.util.UUID;

public class VideoFileNotAccessibleException extends RuntimeException {

    public VideoFileNotAccessibleException(UUID videoId, int resolution) {
        String message = String.format(
                "Video with id %s and resolution %d is not accessible",
                videoId,
                resolution
        );
        super(message);
    }
}
