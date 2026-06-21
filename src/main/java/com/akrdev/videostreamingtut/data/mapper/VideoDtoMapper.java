package com.akrdev.videostreamingtut.data.mapper;

import com.akrdev.videostreamingtut.data.dto.video.VideoDto;
import com.akrdev.videostreamingtut.data.entity.video.Video;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@Primary
public class VideoDtoMapper implements Function<Video, VideoDto> {

    private String getSignedUrl(String originalUrl){
        try{
            return originalUrl;
        } catch (Exception e){
            return originalUrl;
        }
    }

    @Override
    public VideoDto apply(Video video) {
        return VideoDto.builder()
                .videoId(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .thumbnailUrl(getSignedUrl(video.getThumbnail()))
                .uploadedOn(video.getUploadTimestamp())
                .lastUpdatedOn(video.getLastUpdated())
                .build();
    }
}
