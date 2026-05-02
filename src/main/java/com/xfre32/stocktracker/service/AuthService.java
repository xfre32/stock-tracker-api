package com.xfre32.stocktracker.service;

import com.xfre32.stocktracker.dto.auth.*;
import com.xfre32.stocktracker.entity.User;
import com.xfre32.stocktracker.entity.UserPreferences;
import com.xfre32.stocktracker.exception.DuplicateResourceException;
import com.xfre32.stocktracker.repository.UserPreferencesRepository;
import com.xfre32.stocktracker.repository.UserRepository;
import com.xfre32.stocktracker.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserPreferencesRepository preferencesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already registered");
        }

        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);

        // Create default preferences
        preferencesRepository.save(UserPreferences.builder()
                .user(user)
                .theme("dark-theme")
                .build());

        return generateTokens(user.getUsername());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        var user = userRepository.findByUsername(request.username()).orElseThrow();
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return generateTokens(user.getUsername());
    }

    public AuthResponse refresh(RefreshRequest request) {
        if (!tokenProvider.validateToken(request.refreshToken())
                || !tokenProvider.isRefreshToken(request.refreshToken())) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        String username = tokenProvider.getUsernameFromToken(request.refreshToken());
        return generateTokens(username);
    }

    private AuthResponse generateTokens(String username) {
        return new AuthResponse(
                tokenProvider.generateAccessToken(username),
                tokenProvider.generateRefreshToken(username),
                username
        );
    }
}
