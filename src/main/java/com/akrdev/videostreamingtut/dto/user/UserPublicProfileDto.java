package com.akrdev.videostreamingtut.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicProfileDto {
    private String username;
    private String firstName;
    private String lastName;
    private LocalDateTime createdDate;
}