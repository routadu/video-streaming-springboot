package com.akrdev.videostreamingtut.service.user;

import com.akrdev.videostreamingtut.dto.jwt.JwtAuthenticationResponse;
import com.akrdev.videostreamingtut.dto.user.LoginRequest;
import com.akrdev.videostreamingtut.dto.user.RegisterRequest;
import com.akrdev.videostreamingtut.dto.user.UserDto;
import com.akrdev.videostreamingtut.entity.user.User;
import com.akrdev.videostreamingtut.entity.user.UserDetailsImpl;
import com.akrdev.videostreamingtut.exception.UserAlreadyExistsException;
import com.akrdev.videostreamingtut.exception.UserNotFoundException;
import com.akrdev.videostreamingtut.mapper.RegisterRequestUserMapper;
import com.akrdev.videostreamingtut.mapper.UserDtoMapper;
import com.akrdev.videostreamingtut.repository.UserRepository;
import com.akrdev.videostreamingtut.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final RegisterRequestUserMapper requestToUserMapper;
    private final UserDtoMapper userDtoMapper;
    private final PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

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
        Optional<User> existingUser = findByEmail(user.getEmail());
        if(existingUser.isPresent()) {
            throw new UserAlreadyExistsException(user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRole() == null)
            user.setRole("ROLE_USER");
        return repository.save(user);
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
                        request.getEmail(),
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

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public User findByIdOrThrow(Long id) throws UserNotFoundException {
        return findById(id)
                .orElseThrow(() -> UserNotFoundException.id(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public User findByEmailOrThrow(String email) {
        return findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.email(email));
    }

    @Override
    public User loadUserByUsername(String email) {
        return findByEmailOrThrow(email);
    }
}
