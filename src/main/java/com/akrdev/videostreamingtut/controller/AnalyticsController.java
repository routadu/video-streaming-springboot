package com.akrdev.videostreamingtut.controller;

import com.akrdev.videostreamingtut.dto.video.ContinueWatchingDto;
import com.akrdev.videostreamingtut.dto.video.ContinueWatchingUpdateRequest;
import com.akrdev.videostreamingtut.entity.user.UserDetailsImpl;
import com.akrdev.videostreamingtut.service.analytics.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/continuewatching")
    public ResponseEntity<Map<String, ContinueWatchingDto>> getContinueWatchingForUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Optional<ContinueWatchingDto> dto = analyticsService.findContinueWatchingByUser(userDetails.getUsername());
        return ResponseEntity.ok()
                .body(Collections.singletonMap("continuewatching", dto.orElse(null)));
    }

    @PatchMapping("/continuewatching")
    public ResponseEntity<?> updateContinueWatching(
            @RequestBody @Validated ContinueWatchingUpdateRequest updateRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        updateRequest.setUsername(userDetails.getUsername());
        analyticsService.updateContinueWatchingInfo(updateRequest);
        return ResponseEntity.ok().build();
    }
}
