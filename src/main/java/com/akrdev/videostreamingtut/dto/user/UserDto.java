package com.akrdev.videostreamingtut.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotBlank
    @Length(min = 5, max = 50)
    private String firstName;

    @Length(min = 5, max = 50)
    private String lastName;

    @Email
    private String email;

    @NotBlank
    private String password;
}
