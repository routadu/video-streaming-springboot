package com.akrdev.videostreamingtut.service.analytics;

import com.akrdev.videostreamingtut.data.dto.video.ContinueWatchingDto;
import com.akrdev.videostreamingtut.data.dto.video.ContinueWatchingUpdateRequest;

import java.util.Optional;

public interface AnalyticsService {
    Optional<ContinueWatchingDto> findContinueWatchingByUser(String username);
    void updateContinueWatchingInfo(ContinueWatchingUpdateRequest request);
}
