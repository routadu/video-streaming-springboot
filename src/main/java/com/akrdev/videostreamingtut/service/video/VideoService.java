package com.akrdev.videostreamingtut.service.video;

import com.akrdev.videostreamingtut.data.dto.video.VideoDto;
import com.akrdev.videostreamingtut.data.dto.video.VideoListDto;
import com.akrdev.videostreamingtut.data.entity.video.Video;
import com.akrdev.videostreamingtut.exception.video.VideoNotFoundException;
import org.springframework.core.io.Resource;

import java.util.Optional;
import java.util.UUID;

public interface VideoService {
    Video saveVideo(Video video);
    Optional<Video> findById(UUID videoId);
    Video findByIdOrThrow(UUID videoId) throws VideoNotFoundException;
    VideoDto findDtoByIdOrThrow(UUID videoId) throws VideoNotFoundException;
    VideoListDto findAllVideos();
    VideoListDto findAllByQuery(String query);
    VideoListDto findAllByUsername(String username);
    Resource getMasterFile(UUID videoId);
    Resource getThumbnailFile(UUID videoId);
}
