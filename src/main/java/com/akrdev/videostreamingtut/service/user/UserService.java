package com.akrdev.videostreamingtut.service.user;


import com.akrdev.videostreamingtut.dto.jwt.JwtAuthenticationResponse;
import com.akrdev.videostreamingtut.dto.user.LoginRequest;
import com.akrdev.videostreamingtut.dto.user.RegisterRequest;
import com.akrdev.videostreamingtut.dto.user.UserDto;
import com.akrdev.videostreamingtut.entity.user.User;
import com.akrdev.videostreamingtut.exception.UserAlreadyExistsException;
import com.akrdev.videostreamingtut.exception.UserNotFoundException;

import java.util.Optional;

public interface UserService {
    User registerUser(User user) throws UserAlreadyExistsException;
    UserDto registerUser(RegisterRequest request) throws UserAlreadyExistsException;
    JwtAuthenticationResponse loginUser(LoginRequest request);
    Optional<User> findById(Long id);
    User findByIdOrThrow(Long id) throws UserNotFoundException;
    Optional<User> findByEmail(String email);
    User findByEmailOrThrow(String email);
    User loadUserByUsername(String email);

}
