package com.akrdev.videostreamingtut.service.process;

import com.akrdev.videostreamingtut.dto.video.UploadedVideoDto;

public interface VideoProcessingService {
    void processVideo(UploadedVideoDto uploadedVideo);
}
