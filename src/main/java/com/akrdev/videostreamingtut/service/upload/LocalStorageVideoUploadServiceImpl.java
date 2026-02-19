package com.akrdev.videostreamingtut.service.upload;

import com.akrdev.videostreamingtut.dto.video.VideoUploadRequest;
import com.akrdev.videostreamingtut.dto.video.VideoDto;
import com.akrdev.videostreamingtut.dto.video.UploadedVideoDto;
import com.akrdev.videostreamingtut.entity.video.Video;
import com.akrdev.videostreamingtut.exception.VideoUploadException;
import com.akrdev.videostreamingtut.service.process.VideoProcessingService;
import com.akrdev.videostreamingtut.service.video.VideoService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalStorageVideoUploadServiceImpl implements VideoUploadService {

    private final VideoService videoService;
    private final VideoProcessingService videoProcessingService;

    @Value("${video.default-local-directory}")
    private String localStoragePath;

    @Value("${video.default-local-hls-directory}")
    private String localHlsStoragePath;

    @PostConstruct
    public void init() {
        try{
            log.info("Creating directory: {}",  localStoragePath);
            Files.createDirectories(Path.of(localStoragePath));
            log.info("Creating directory: {}",  localHlsStoragePath);
            Files.createDirectories(Path.of(localHlsStoragePath));
        } catch (IOException e){
            log.error("Error while creating directory", e);
        }
    }

    public VideoDto uploadVideo(VideoUploadRequest videoRequest, MultipartFile file, Long ownerId) {

        try{
            //Saving video to get UUID
            Video video = Video.builder()
                    .title(videoRequest.getTitle())
                    .description(videoRequest.getDescription())
                    .contentType(videoRequest.getContentType())
                    .videos(new HashSet<>())
                    .build();
            Video savedVideo = videoService.saveVideo(video);

            //Creating video path
            Path videoPath = Paths.get(localStoragePath, video.getId().toString());
            if(!Files.exists(videoPath)){
                Files.createDirectories(videoPath);
            }

            //Extracting extension
            String fileName = file.getOriginalFilename();
            String extension;
            if(fileName == null || !fileName.contains(".")){
                extension = ".mp4";
            } else {
                extension = fileName.substring(fileName.lastIndexOf("."));
            }
            Path videoFullPath = videoPath.resolve("1080p" + extension);

            //Saving the video
            Files.copy(
                    file.getInputStream(),
                    videoFullPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            UploadedVideoDto uploadedVideoDto = UploadedVideoDto.builder()
                    .videoId(savedVideo.getId())
                    .filePath(videoFullPath.toString())
                    .build();

            videoProcessingService.processVideo(uploadedVideoDto);

            return VideoDto.builder()
                    .videoId(savedVideo.getId())
                    .title(savedVideo.getTitle())
                    .description(savedVideo.getDescription())
                    .build();
        } catch (IOException e){
            log.error("Unable to upload video", e);
            throw new VideoUploadException("Unable to upload video");
        }

    }
}
