package com.akrdev.videostreamingtut.exception;

import java.util.UUID;

public class VideoFileNotFoundException extends RuntimeException {
    public VideoFileNotFoundException(UUID videoId) {
        super("No video file with id " + videoId + " found");
    }

    public VideoFileNotFoundException(UUID videoId, int resolution) {
        super(String.format("No video file with id %s and resolution %d found",
                videoId,
                resolution
                )
        );
    }
}
