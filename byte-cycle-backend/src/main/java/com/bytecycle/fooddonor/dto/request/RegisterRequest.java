package com.bytecycle.fooddonor.dto.request;

import com.bytecycle.fooddonor.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request payload")
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Schema(description = "User's full name", example = "Nithish Kumar")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Schema(description = "User's email address", example = "nithish@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    @Schema(description = "User's password (min 8 chars, must have upper, lower, digit)", example = "Password@123")
    private String password;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Please provide a valid 10-digit Indian mobile number")
    @Schema(description = "User's phone number", example = "9876543210")
    private String phone;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Schema(description = "User's address", example = "123 Main Street")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City name cannot exceed 100 characters")
    @Schema(description = "User's city", example = "Chennai")
    private String city;

    @Size(max = 100, message = "State name cannot exceed 100 characters")
    @Schema(description = "User's state", example = "Tamil Nadu")
    private String state;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Please provide a valid 6-digit pincode")
    @Schema(description = "User's pincode", example = "600001")
    private String pincode;

    @NotNull(message = "Role is required")
    @Schema(description = "User role - DONOR or RECEIVER", example = "DONOR")
    private UserRole role;
}
