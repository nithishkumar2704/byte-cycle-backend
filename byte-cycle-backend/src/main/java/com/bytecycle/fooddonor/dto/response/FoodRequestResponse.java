package com.bytecycle.fooddonor.dto.response;

import com.bytecycle.fooddonor.enums.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for food request data returned in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Food request response object")
public class FoodRequestResponse {

    @Schema(description = "Request ID", example = "1")
    private Long id;

    @Schema(description = "Message to donor", example = "We are a shelter home with 15 residents.")
    private String message;

    @Schema(description = "Request status", example = "PENDING")
    private RequestStatus status;

    @Schema(description = "Preferred pickup time")
    private LocalDateTime pickupScheduledTime;

    @Schema(description = "Notes from donor", example = "Please come by 5 PM.")
    private String donorNotes;

    // Receiver info
    @Schema(description = "Receiver's user ID", example = "5")
    private Long receiverId;

    @Schema(description = "Receiver's full name", example = "Priya Sharma")
    private String receiverName;

    @Schema(description = "Receiver's email", example = "priya@example.com")
    private String receiverEmail;

    @Schema(description = "Receiver's phone", example = "9123456789")
    private String receiverPhone;

    // Donation info
    @Schema(description = "Donation ID", example = "2")
    private Long donationId;

    @Schema(description = "Donation title", example = "Fresh Cooked Rice and Dal")
    private String donationTitle;

    @Schema(description = "Donation pickup address", example = "45 Gandhi Nagar, Chennai")
    private String donationAddress;

    @Schema(description = "Donor's name", example = "Rajesh Kumar")
    private String donorName;

    @Schema(description = "Request created timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Request last updated timestamp")
    private LocalDateTime updatedAt;
}
