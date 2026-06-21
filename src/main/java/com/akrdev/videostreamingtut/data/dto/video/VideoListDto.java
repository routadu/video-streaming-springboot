package com.akrdev.videostreamingtut.data.dto.video;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class VideoListDto {
    private List<VideoDto> videos;
}
