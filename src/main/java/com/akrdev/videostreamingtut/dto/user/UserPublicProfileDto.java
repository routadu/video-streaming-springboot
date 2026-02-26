package com.akrdev.videostreamingtut.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserPublicProfileDto {
    private String username;
    private String firstName;
    private String lastName;
    private LocalDateTime createdDate;
}