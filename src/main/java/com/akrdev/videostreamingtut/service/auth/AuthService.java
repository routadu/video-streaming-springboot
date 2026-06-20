package com.akrdev.videostreamingtut.service.auth;

import com.akrdev.videostreamingtut.dto.auth.LoginRequest;
import com.akrdev.videostreamingtut.dto.auth.RegisterRequest;
import com.akrdev.videostreamingtut.dto.jwt.JwtAuthenticationResponse;
import com.akrdev.videostreamingtut.dto.user.UserDto;
import com.akrdev.videostreamingtut.entity.user.User;
import com.akrdev.videostreamingtut.exception.user.UserAlreadyExistsException;

public interface AuthService {
    User registerUser(User user) throws UserAlreadyExistsException;
    UserDto registerUser(RegisterRequest request) throws UserAlreadyExistsException;
    JwtAuthenticationResponse loginUser(LoginRequest request);
}
