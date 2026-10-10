package com.akrdev.videostreamingtut.exception.video;

import java.util.UUID;

public class VideoNotFoundException extends VideoException {
    public VideoNotFoundException(UUID videoId) {
        super("Video with id " + videoId + " not found");
    }
}
