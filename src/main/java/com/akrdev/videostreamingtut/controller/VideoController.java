package com.akrdev.videostreamingtut.controller;

import com.akrdev.videostreamingtut.dto.comment.CommentRequestDto;
import com.akrdev.videostreamingtut.dto.comment.CommentResponseDto;
import com.akrdev.videostreamingtut.dto.video.VideoDto;
import com.akrdev.videostreamingtut.dto.video.VideoListDto;
import com.akrdev.videostreamingtut.dto.video.VideoUploadRequest;
import com.akrdev.videostreamingtut.entity.user.UserDetailsImpl;
import com.akrdev.videostreamingtut.service.comment.CommentService;
import com.akrdev.videostreamingtut.service.video.VideoService;
import com.akrdev.videostreamingtut.service.upload.VideoUploadService;
import com.akrdev.videostreamingtut.service.videofile.VideoFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final VideoService videoService;
    private final VideoFileService  videoFileService;
    private final VideoUploadService videoUploadService;
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<VideoDto> uploadVideo(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(name = "contentType", defaultValue = "application/octet-stream") String contentType,
            @RequestPart(value = "file")  MultipartFile file,
            @RequestPart(value = "thumbnail", required = false)  MultipartFile thumbnail,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {
        VideoUploadRequest videoUploadRequest = VideoUploadRequest.builder()
                .title(title)
                .description(description)
                .contentType(contentType)
                .build();
        VideoDto response = videoUploadService.uploadVideo(
                videoUploadRequest,
                file,
                thumbnail,
                userDetails.getUsername()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<VideoListDto> getAllVideos(){
        return ResponseEntity.ok(videoService.findAllVideos());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<VideoListDto> getVideosByUser(@PathVariable String username){
        return ResponseEntity.ok(videoService.findAllByUsername(username));
    }

    @GetMapping("/search")
    public ResponseEntity<VideoListDto> searchVideos(@RequestParam String query){
        return ResponseEntity.ok()
                .body(videoService.findAllByQuery(query));
    }

    @GetMapping("/recommend")
    public ResponseEntity<VideoListDto> getRecommendVideos(){
        return ResponseEntity.ok(videoService.findAllVideos());
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<Map<String,VideoDto>> getVideoById(@PathVariable UUID videoId){
        VideoDto video = videoService.findDtoByIdOrThrow(videoId);
        Map<String,VideoDto> response = Map.of("video", video);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{videoId}/master.m3u8")
    public ResponseEntity<Resource> serveMasterPlaylist(@PathVariable UUID videoId) {
        Resource resource = videoService.getMasterFile(videoId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
                .body(resource);
    }

    @GetMapping("/{videoId}/thumbnail")
    public ResponseEntity<Resource> serveThumbnail(@PathVariable UUID videoId) {
        Resource resource = videoService.getThumbnailFile(videoId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .cacheControl(CacheControl.maxAge(7, TimeUnit.HOURS))
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

    @PostMapping("/{videoId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable UUID videoId,
            @RequestBody @Validated CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        requestDto.setVideoId(videoId);
        requestDto.setAuthorUsername(userDetails.getUsername());
        CommentResponseDto createdComment = commentService.createComment(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdComment);
    }

    @GetMapping("/{videoId}/comments")
    public ResponseEntity<Page<CommentResponseDto>> getTopLevelComments(
            @PathVariable UUID videoId,
            Pageable pageable
    ) {
        Page<CommentResponseDto> comments = commentService.getTopLevelComments(videoId, pageable);
        return ResponseEntity.ok(comments);
    }

}
