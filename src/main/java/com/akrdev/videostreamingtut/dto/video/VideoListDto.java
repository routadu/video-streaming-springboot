package com.akrdev.videostreamingtut.dto.video;

import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Setter
@Builder
public class VideoListDto {
    private List<VideoDto> videos;
}
