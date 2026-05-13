package com.bytecycle.fooddonor.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for creating or updating a food donation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Food donation create/update payload")
public class DonationRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    @Schema(description = "Short title for the donation", example = "Fresh Cooked Rice and Dal")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Schema(description = "Detailed description of the food", example = "Freshly cooked rice and dal, enough for 10 people. Made this morning.")
    private String description;

    @NotBlank(message = "Food type is required")
    @Size(max = 100, message = "Food type cannot exceed 100 characters")
    @Schema(description = "Type/category of food", example = "Cooked Meal")
    private String foodType;

    @NotBlank(message = "Quantity is required")
    @Size(max = 50, message = "Quantity cannot exceed 50 characters")
    @Schema(description = "Amount/quantity of food", example = "5 kg / serves 10 people")
    private String quantity;

    @Future(message = "Expiry time must be a future date/time")
    @Schema(description = "When the food expires / last pickup time", example = "2024-12-31T18:00:00")
    private LocalDateTime expiryTime;

    @NotBlank(message = "Pickup address is required")
    @Size(max = 255, message = "Pickup address cannot exceed 255 characters")
    @Schema(description = "Address where food can be picked up", example = "45 Gandhi Nagar, Anna Salai")
    private String pickupAddress;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    @Schema(description = "City of pickup", example = "Chennai")
    private String city;

    @Size(max = 100)
    @Schema(description = "State of pickup", example = "Tamil Nadu")
    private String state;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Please provide a valid 6-digit pincode")
    @Schema(description = "Pincode of pickup location", example = "600001")
    private String pincode;

    @Schema(description = "Is the food vegetarian?", example = "true")
    private Boolean isVegetarian;

    @Min(value = 1, message = "Serves count must be at least 1")
    @Schema(description = "Number of people it can serve", example = "10")
    private Integer servesCount;

    @Schema(description = "URL of food image (optional)", example = "https://example.com/image.jpg")
    private String imageUrl;
}
