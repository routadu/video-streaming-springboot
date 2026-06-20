package com.akrdev.videostreamingtut.dto.video;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ThumbnailDto {
    private UUID videosId;
    private String thumbnailUrl;
}
