package com.bytecycle.fooddonor.dto.response;

import com.bytecycle.fooddonor.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user profile data in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile response object")
public class UserProfileResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Full name", example = "Nithish Kumar")
    private String fullName;

    @Schema(description = "Email address", example = "nithish@example.com")
    private String email;

    @Schema(description = "Phone number", example = "9876543210")
    private String phone;

    @Schema(description = "Address", example = "123 Main Street")
    private String address;

    @Schema(description = "City", example = "Chennai")
    private String city;

    @Schema(description = "State", example = "Tamil Nadu")
    private String state;

    @Schema(description = "Pincode", example = "600001")
    private String pincode;

    @Schema(description = "User role", example = "DONOR")
    private UserRole role;

    @Schema(description = "Is account active", example = "true")
    private Boolean isActive;

    @Schema(description = "Account created date")
    private LocalDateTime createdAt;

    @Schema(description = "Total donations count (for donors)", example = "5")
    private Long totalDonations;

    @Schema(description = "Total requests count (for receivers)", example = "3")
    private Long totalRequests;
}
