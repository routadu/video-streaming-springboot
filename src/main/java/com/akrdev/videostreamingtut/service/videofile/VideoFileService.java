package com.akrdev.videostreamingtut.service.videofile;

import com.akrdev.videostreamingtut.entity.video.videofile.VideoFile;
import com.akrdev.videostreamingtut.entity.video.videofile.processingstatus.ProcessingStatus;
import com.akrdev.videostreamingtut.exception.VideoFileNotFoundException;
import org.springframework.core.io.Resource;

import java.util.Optional;
import java.util.UUID;

public interface VideoFileService {
    VideoFile saveFile(VideoFile videoFile);
    Optional<VideoFile> findByVideoIdAndResolution(UUID videoId, int resolution);
    VideoFile findByVideoIdAndResolutionOrThrow(UUID videoId, int resolution);
    VideoFile updateVideoFileProcessingStatus(UUID videoId, int resolution, ProcessingStatus newStatus) throws VideoFileNotFoundException;
    Resource getSegmentFile(UUID videoId, String resolution, String segmentFileName);
    void performCleanup();
}
