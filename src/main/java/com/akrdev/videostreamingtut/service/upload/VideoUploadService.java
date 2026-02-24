package com.akrdev.videostreamingtut.service.upload;

import com.akrdev.videostreamingtut.dto.video.VideoUploadRequest;
import com.akrdev.videostreamingtut.dto.video.VideoDto;
import com.akrdev.videostreamingtut.exception.VideoUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface VideoUploadService {
    VideoDto uploadVideo(VideoUploadRequest videoRequest, MultipartFile file, MultipartFile thumbnail, Long ownerId) throws VideoUploadException;
}
