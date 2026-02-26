package com.akrdev.videostreamingtut.dto.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContinueWatchingDto {
    private UUID videoId;
    private String title;
    private String thumbnailUrl;
    private long lastPlaybackSecond;
}
