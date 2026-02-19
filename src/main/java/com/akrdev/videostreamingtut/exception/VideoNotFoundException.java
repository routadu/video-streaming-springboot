package com.akrdev.videostreamingtut.exception;

import java.util.UUID;

public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException(UUID videoId) {
        super("Video with id " + videoId + " not found");
    }
}
