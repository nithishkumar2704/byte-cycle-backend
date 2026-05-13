package com.bytecycle.fooddonor.dto.response;

import com.bytecycle.fooddonor.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response (login & register).
 * Returns JWT token and basic user info.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response with JWT token")
public class AuthResponse {

    @Schema(description = "JWT Bearer token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Token type", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "User's full name", example = "Nithish Kumar")
    private String fullName;

    @Schema(description = "User's email", example = "nithish@example.com")
    private String email;

    @Schema(description = "User's role", example = "DONOR")
    private UserRole role;

    @Schema(description = "Response message", example = "Login successful")
    private String message;
}
