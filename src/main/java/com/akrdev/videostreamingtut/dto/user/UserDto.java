package com.akrdev.videostreamingtut.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDto {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private String role;
}