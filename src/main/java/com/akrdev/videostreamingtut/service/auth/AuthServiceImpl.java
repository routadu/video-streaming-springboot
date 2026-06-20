package com.akrdev.videostreamingtut.service.auth;

import com.akrdev.videostreamingtut.dto.auth.LoginRequest;
import com.akrdev.videostreamingtut.dto.auth.RegisterRequest;
import com.akrdev.videostreamingtut.dto.jwt.JwtAuthenticationResponse;
import com.akrdev.videostreamingtut.dto.user.UserDto;
import com.akrdev.videostreamingtut.entity.user.User;
import com.akrdev.videostreamingtut.entity.user.UserDetailsImpl;
import com.akrdev.videostreamingtut.exception.UserAlreadyExistsException;
import com.akrdev.videostreamingtut.mapper.RegisterRequestUserMapper;
import com.akrdev.videostreamingtut.mapper.UserDtoMapper;
import com.akrdev.videostreamingtut.security.jwt.JwtUtils;
import com.akrdev.videostreamingtut.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RegisterRequestUserMapper requestToUserMapper;
    private final UserDtoMapper userDtoMapper;
    private final PasswordEncoder passwordEncoder;

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtUtils(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public User registerUser(User user) throws UserAlreadyExistsException {
        Optional<User> existingUser = userService.findByUsernameOrEmail(user.getUsername(), user.getEmail());
        if(existingUser.isPresent()) {
            throw new UserAlreadyExistsException(user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRole() == null)
            user.setRole("ROLE_USER");
        return userService.saveUser(user);
    }

    @Override
    public UserDto registerUser(RegisterRequest request) {
        User newUser = requestToUserMapper.apply(request);
        User registeredUser = registerUser(newUser);
        return userDtoMapper.apply(registeredUser);
    }

    @Override
    public JwtAuthenticationResponse loginUser(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        if(userDetails == null) {
            return JwtAuthenticationResponse.builder()
                    .token(null)
                    .response("Failed to login")
                    .build();
        }

        SecurityContextHolder.getContext().setAuthentication(auth);

        return JwtAuthenticationResponse.builder()
                .token(jwtUtils.generateToken(userDetails))
                .response("User logged in successfully")
                .build();
    }
}
