package com.osrsflip.controller;

import com.osrsflip.model.dto.AuthResponse;
import com.osrsflip.model.dto.LoginRequest;
import com.osrsflip.model.dto.RegisterRequest;
import com.osrsflip.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "User registration and login")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new account")
    @ApiResponse(responseCode = "200", description = "Account created, JWT returned")
    @ApiResponse(responseCode = "409", description = "Username or email already taken")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(userService.register(req));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive a JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful, JWT returned")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(userService.login(req));
    }
}
