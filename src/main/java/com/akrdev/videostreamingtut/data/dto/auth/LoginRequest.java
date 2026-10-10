package com.akrdev.videostreamingtut.data.dto.auth;

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
