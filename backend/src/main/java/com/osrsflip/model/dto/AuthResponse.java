package com.osrsflip.model.dto;

public record AuthResponse(
        String token,
        String username,
        String role
) {}
