package com.akrdev.videostreamingtut.service.process;

import com.akrdev.videostreamingtut.data.dto.video.UploadedVideoDto;

public interface VideoProcessingService {
    void generateThumbnail(UploadedVideoDto uploadedVideo);
    void processVideo(UploadedVideoDto uploadedVideo);
}
