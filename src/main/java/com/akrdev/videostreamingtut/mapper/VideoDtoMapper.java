package com.akrdev.videostreamingtut.mapper;

import com.akrdev.videostreamingtut.dto.video.VideoDto;
import com.akrdev.videostreamingtut.entity.video.Video;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class VideoDtoMapper implements Function<Video, VideoDto> {
    @Override
    public VideoDto apply(Video video) {
        return VideoDto.builder()
                .videoId(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .uploadedOn(video.getUploadTimestamp())
                .lastUpdatedOn(video.getLastUpdated())
                .build();
    }
}
