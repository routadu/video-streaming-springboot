package com.akrdev.videostreamingtut.exception.video;

import java.util.UUID;

public class VideoFileNotFoundException extends VideoException {
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
