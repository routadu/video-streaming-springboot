package com.akrdev.videostreamingtut.service.video;

import com.akrdev.videostreamingtut.dto.video.VideoListDto;
import com.akrdev.videostreamingtut.entity.video.Video;
import com.akrdev.videostreamingtut.exception.VideoNotFoundException;
import org.springframework.core.io.Resource;

import java.util.Optional;
import java.util.UUID;

public interface VideoService {
    Video saveVideo(Video video);
    Optional<Video> findById(UUID videoId);
    Video findByIdOrThrow(UUID videoId) throws VideoNotFoundException;
    VideoListDto findAllVideos();
    Resource getMasterFile(UUID videoId);
}
