package com.iss.fitness.service;

import com.iss.fitness.domain.user.User;
import com.iss.fitness.security.JwtService;
import com.iss.fitness.web.dto.auth.AuthResponse;
import com.iss.fitness.web.dto.auth.LoginRequest;
import com.iss.fitness.web.dto.auth.SignupRequest;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse signup(SignupRequest request) {
        User user = userService.registerUser(request);
        return toAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (BadCredentialsException ex) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userService.findByUsername(request.username());
        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(User user) {
        String token = jwtService.generateToken(
            user,
            Map.of("role", user.getRole().name(), "uid", user.getId().toString())
        );

        return new AuthResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getName(),
            user.getRole().name()
        );
    }
}
