package com.bytecycle.fooddonor.dto.response;

import com.bytecycle.fooddonor.enums.DonationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for donation data returned in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Food donation response object")
public class DonationResponse {

    @Schema(description = "Donation ID", example = "1")
    private Long id;

    @Schema(description = "Donation title", example = "Fresh Cooked Rice and Dal")
    private String title;

    @Schema(description = "Detailed description", example = "Freshly cooked rice and dal for 10 people.")
    private String description;

    @Schema(description = "Type of food", example = "Cooked Meal")
    private String foodType;

    @Schema(description = "Quantity of food", example = "5 kg / serves 10 people")
    private String quantity;

    @Schema(description = "Expiry time of the food")
    private LocalDateTime expiryTime;

    @Schema(description = "Pickup address", example = "45 Gandhi Nagar, Anna Salai")
    private String pickupAddress;

    @Schema(description = "City", example = "Chennai")
    private String city;

    @Schema(description = "State", example = "Tamil Nadu")
    private String state;

    @Schema(description = "Pincode", example = "600001")
    private String pincode;

    @Schema(description = "Donation status", example = "AVAILABLE")
    private DonationStatus status;

    @Schema(description = "Is vegetarian", example = "true")
    private Boolean isVegetarian;

    @Schema(description = "Number of people it serves", example = "10")
    private Integer servesCount;

    @Schema(description = "Food image URL")
    private String imageUrl;

    @Schema(description = "Donor's ID", example = "3")
    private Long donorId;

    @Schema(description = "Donor's full name", example = "Rajesh Kumar")
    private String donorName;

    @Schema(description = "Donor's city", example = "Chennai")
    private String donorCity;

    @Schema(description = "Donor's phone", example = "9876543210")
    private String donorPhone;

    @Schema(description = "Created timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last updated timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Number of requests for this donation", example = "2")
    private Long requestCount;
}
