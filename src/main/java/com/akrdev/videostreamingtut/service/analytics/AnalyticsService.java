package com.akrdev.videostreamingtut.service.analytics;

import com.akrdev.videostreamingtut.dto.video.ContinueWatchingDto;
import com.akrdev.videostreamingtut.dto.video.ContinueWatchingUpdateRequest;

import java.util.Optional;

public interface AnalyticsService {
    Optional<ContinueWatchingDto> findContinueWatchingByUser(String username);
    void updateContinueWatchingInfo(ContinueWatchingUpdateRequest request);
}
