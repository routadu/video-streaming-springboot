package com.akrdev.videostreamingtut.service.analytics;

import com.akrdev.videostreamingtut.dto.video.ContinueWatchingDto;
import com.akrdev.videostreamingtut.dto.video.ContinueWatchingUpdateRequest;
import com.akrdev.videostreamingtut.entity.video.Video;
import com.akrdev.videostreamingtut.entity.video.continuewatching.ContinueWatching;
import com.akrdev.videostreamingtut.repository.ContinueWatchingRepository;
import com.akrdev.videostreamingtut.service.video.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService{

    private final ContinueWatchingRepository continueWatchingRepository;

    private VideoService videoService;

    @Autowired
    @Lazy
    public void setVideoService(VideoService videoService) {
        this.videoService = videoService;
    }

    @Override
    public Optional<ContinueWatchingDto> findContinueWatchingByUser(String username) {
        Optional<ContinueWatching> eventOptional = continueWatchingRepository.findById(username);
        if(eventOptional.isEmpty()){
            return Optional.empty();
        }
        ContinueWatching event = eventOptional.get();
        Optional<Video> videoOptional = videoService.findById(event.getVideoId());
        if(videoOptional.isEmpty()){
            return Optional.empty();
        }
        Video video = videoOptional.get();
        ContinueWatchingDto dto = ContinueWatchingDto.builder()
                .videoId(event.getVideoId())
                .lastPlaybackSecond(event.getLastPlaybackSecond())
                .title(video.getTitle())
                .thumbnailUrl(video.getThumbnail())
                .build();
        return Optional.of(dto);
    }

    @Override
    public void updateContinueWatchingInfo(ContinueWatchingUpdateRequest request) {
        ContinueWatching event = ContinueWatching.builder()
                .username(request.getUsername())
                .videoId(request.getVideoId())
                .lastPlaybackSecond(request.getLastPlaybackSecond())
                .build();
        continueWatchingRepository.save(event);
    }
}
