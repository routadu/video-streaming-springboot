package com.akrdev.videostreamingtut.service.upload;

import com.akrdev.videostreamingtut.dto.video.VideoUploadRequest;
import com.akrdev.videostreamingtut.dto.video.VideoDto;
import com.akrdev.videostreamingtut.dto.video.UploadedVideoDto;
import com.akrdev.videostreamingtut.entity.user.User;
import com.akrdev.videostreamingtut.entity.video.Video;
import com.akrdev.videostreamingtut.exception.VideoUploadException;
import com.akrdev.videostreamingtut.service.process.VideoProcessingService;
import com.akrdev.videostreamingtut.service.user.UserService;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalStorageVideoUploadServiceImpl implements VideoUploadService {

    private final VideoService videoService;
    private final VideoProcessingService videoProcessingService;
    private final UserService userService;

    private static final String thumbnailBaseUrl = "http://localhost:8080/api/v1/videos/%s/thumbnail";

    @Value("${video.default-local-directory}")
    private String videosBasePath;

    @Value("${thumbnail.default-local-directory}")
    private String thumbnailLocalDirectory;

    @PostConstruct
    public void init() {
        try{
            Path videosBasePathObj = Path.of(videosBasePath);
            Path thumbnailPathObj = Path.of(thumbnailLocalDirectory);

            if(Files.notExists(videosBasePathObj)){
                log.info("Creating directory: {}", videosBasePath);
                Files.createDirectories(videosBasePathObj);
            }
            if(Files.notExists(thumbnailPathObj)){
                log.info("Creating thumbnail directory: {}",  thumbnailLocalDirectory);
                Files.createDirectories(thumbnailPathObj);
            }
        } catch (IOException e){
            log.error("Error while creating directory", e);
        }
    }

    public boolean uploadThumbnail(UUID videoId, MultipartFile thumbnail) {

        if(thumbnail == null || thumbnail.isEmpty()){
            log.info("Thumbnail is not provided");
            return false;
        }

        try{
            Path thumbnailVideoPath = Paths.get(thumbnailLocalDirectory, videoId.toString());
            if(Files.notExists(thumbnailVideoPath)){
                Files.createDirectories(thumbnailVideoPath);
            }

            Path thumbnailFullPath = thumbnailVideoPath.resolve("thumbnail.jpg");

            //Saving the thumbnail
            Files.copy(
                    thumbnail.getInputStream(),
                    thumbnailFullPath,
                    StandardCopyOption.REPLACE_EXISTING
            );
            return true;
        } catch (IOException e){
            log.error("Error while uploading thumbnail", e);
            return false;
        }

    }

    public VideoDto uploadVideo(VideoUploadRequest videoRequest, MultipartFile file, MultipartFile thumbnail, Long ownerId) {

        try{
            User user = userService.findByIdOrThrow(ownerId);

            //Saving video to get UUID
            Video video = Video.builder()
                    .title(videoRequest.getTitle())
                    .description(videoRequest.getDescription())
                    .contentType(videoRequest.getContentType())
                    .videos(new HashSet<>())
                    .owner(user)
                    .build();
            Video savedVideo = videoService.saveVideo(video);
            savedVideo.setThumbnail(String.format(thumbnailBaseUrl, savedVideo.getId()));
            videoService.saveVideo(savedVideo);

            boolean thumbnailUploaded = uploadThumbnail(savedVideo.getId(), thumbnail);
            log.info("Provided thumbnail uploaded: {}", thumbnailUploaded);

            //Creating video path
            Path videoPath = Paths.get(videosBasePath, video.getId().toString());
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

            if(!thumbnailUploaded){
                videoProcessingService.generateThumbnail(uploadedVideoDto);
            }
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
