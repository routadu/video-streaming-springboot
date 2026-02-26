package com.akrdev.videostreamingtut.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
public class LoginRequest {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
