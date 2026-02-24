package com.akrdev.videostreamingtut.mapper;

import com.akrdev.videostreamingtut.dto.video.VideoDto;
import com.akrdev.videostreamingtut.entity.video.Video;
import com.akrdev.videostreamingtut.entity.video.resolution.VideoResolution;
import com.akrdev.videostreamingtut.entity.video.videofile.VideoFile;
import org.hibernate.engine.spi.Resolution;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class VideoDtoMapperWithResolution implements Function<Video, VideoDto> {
    @Override
    public VideoDto apply(Video video) {
        List<Integer> resolutionList = video.getVideos().stream()
                .map(VideoFile::getResolution)
                .sorted().toList();
        return VideoDto.builder()
                .videoId(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .uploadedOn(video.getUploadTimestamp())
                .lastUpdatedOn(video.getLastUpdated())
                .resolutions(resolutionList)
                .build();
    }
}