package com.akrdev.videostreamingtut.dto.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedVideoDto {

    private UUID videoId;
    private String filePath;

    /**
     * This is a helper method which returns Paths.get(filePath).
     * The path returned is the absolute path of the original video stored
    */
    public Path getVideoPath() {
        return Paths.get(filePath);
    }

    /**
     * This is a helper method which returns Paths.get(videoId).
     * The path returned is the relative path (from default-local-directory) for the base dir of a video
    */
    public Path getVideoBasePath() {
        return Paths.get(videoId.toString());
    }
}
