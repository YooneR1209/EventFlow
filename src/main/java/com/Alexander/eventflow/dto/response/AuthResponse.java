package com.Alexander.eventflow.dto.response;

public record AuthResponse(
        String token,
        String email,
        String firstName,
        String role
) {}