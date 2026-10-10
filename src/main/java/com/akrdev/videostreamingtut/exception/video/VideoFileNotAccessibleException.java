package com.akrdev.videostreamingtut.exception.video;

import java.util.UUID;

public class VideoFileNotAccessibleException extends VideoException {

    public VideoFileNotAccessibleException(UUID videoId, int resolution) {
        String message = String.format(
                "Video with id %s and resolution %d is not accessible",
                videoId,
                resolution
        );
        super(message);
    }
}
