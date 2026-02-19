package com.akrdev.videostreamingtut.entity.video.resolution;

import lombok.Getter;

@Getter
public enum VideoResolution {
    RES_2160P("2160p", 3840, 2160, "15000k"), // 4K
    RES_1440P("1440p", 2560, 1440, "8000k"),  // 2K
    RES_1080P("1080p", 1920, 1080, "5000k"),
    RES_720P("720p", 1280, 720, "2800k"),
    RES_360P("360p", 640, 360, "800k"),
    RES_144P("144p", 256, 144, "400k");

    private final String name;
    private final int width;
    private final int height;
    private final String bitrate;

    VideoResolution(String name, int width, int height, String bitrate) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.bitrate = bitrate;
    }
}
