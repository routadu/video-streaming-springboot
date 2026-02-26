package com.akrdev.videostreamingtut.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Length(min = 3, max = 20)
    private String username;

    @NotBlank
    @Length(min = 3, max = 50)
    private String firstName;

    @Length(max = 50)
    private String lastName;

    @Email
    private String email;

    @NotBlank
    private String password;

    private String roles;
}
