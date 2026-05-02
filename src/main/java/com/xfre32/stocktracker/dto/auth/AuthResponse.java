package com.xfre32.stocktracker.dto.auth;

public record AuthResponse(String accessToken, String refreshToken, String username) {}
