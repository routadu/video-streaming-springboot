package com.akrdev.videostreamingtut.entity.video.continuewatching;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ContinueWatching {

    @Id
    private String username;

    @Column(nullable = false)
    private UUID videoId;

    @Column(nullable = false)
    private long lastPlaybackSecond;
}
