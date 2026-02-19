package com.akrdev.videostreamingtut.controller;

import com.akrdev.videostreamingtut.dto.video.VideoDto;
import com.akrdev.videostreamingtut.dto.video.VideoListDto;
import com.akrdev.videostreamingtut.dto.video.VideoUploadRequest;
import com.akrdev.videostreamingtut.service.video.VideoService;
import com.akrdev.videostreamingtut.service.upload.VideoUploadService;
import com.akrdev.videostreamingtut.service.videofile.VideoFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/videos")
@CrossOrigin("*")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService  videoService;
    private final VideoFileService  videoFileService;
    private final VideoUploadService videoUploadService;

    @PostMapping
    public ResponseEntity<VideoDto> uploadVideo(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(defaultValue = "application/octet-stream") String contentType,
            @RequestParam MultipartFile file
    ) {
        VideoUploadRequest videoUploadRequest = VideoUploadRequest.builder()
                .title(title)
                .description(description)
                .contentType(contentType)
                .build();
        VideoDto response = videoUploadService.uploadVideo(
                videoUploadRequest,
                file,
                1L //TODO: Implement user authentication and provide actual userId based on authentication token
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<VideoListDto> getAllVideos(){
        return ResponseEntity.ok(videoService.findAllVideos());
    }

    @GetMapping("/{videoId}/master.m3u8")
    public ResponseEntity<Resource> serveMasterPlaylist(@PathVariable UUID videoId) {
        Resource resource = videoService.getMasterFile(videoId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
                .body(resource);
    }

    @GetMapping("/{videoId}/{resolution}/{fileName}")
    public ResponseEntity<Resource> serveSegment(
            @PathVariable UUID videoId,
            @PathVariable String resolution,
            @PathVariable String fileName
    ) {

        Resource resource = videoFileService.getSegmentFile(videoId, resolution, fileName);

        // Determine correct Content-Type based on file extension
        String contentType;
        if (fileName.endsWith(".m3u8")) {
            contentType = "application/vnd.apple.mpegurl";
        } else if (fileName.endsWith(".ts")) {
            contentType = "video/mp2t"; // MIME type for MPEG Transport Stream
        } else {
            contentType = "application/octet-stream"; // Fallback
        }

        // Playlists (.m3u8) shouldn't be heavily cached during live streams, but for VOD it's okay.
        // Segments (.ts) can be cached forever because they never change.
        HttpHeaders headers = new HttpHeaders();
        if (fileName.endsWith(".ts")) {
            headers.setCacheControl("public, max-age=31536000");
        } else {
            headers.setCacheControl("no-cache");
        }

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
