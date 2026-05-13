package com.bytecycle.fooddonor.controller;

import com.bytecycle.fooddonor.dto.request.LoginRequest;
import com.bytecycle.fooddonor.dto.request.RegisterRequest;
import com.bytecycle.fooddonor.dto.response.ApiResponse;
import com.bytecycle.fooddonor.dto.response.AuthResponse;
import com.bytecycle.fooddonor.dto.response.UserProfileResponse;
import com.bytecycle.fooddonor.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication endpoints.
 * Handles user registration, login, and profile retrieval.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration, login, and profile APIs")
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Register a new user (DONOR or RECEIVER).
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new DONOR or RECEIVER account and returns a JWT token."
    )
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse authResponse = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully.", authResponse));
    }

    /**
     * POST /api/auth/login
     * Authenticate user credentials and return a JWT token.
     */
    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticates with email and password. Returns a JWT Bearer token."
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful.", authResponse));
    }

    /**
     * GET /api/auth/me
     * Get the profile of the currently authenticated user.
     * Requires: Any valid JWT token (DONOR or RECEIVER).
     */
    @GetMapping("/me")
    @Operation(
            summary = "Get current user profile",
            description = "Returns the profile of the currently authenticated user."
    )
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        UserProfileResponse profile = authService.getMyProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully.", profile));
    }
}
