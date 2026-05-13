package com.bytecycle.fooddonor.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for a receiver requesting a food donation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Food request payload from a receiver")
public class FoodRequestDto {

    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    @Schema(description = "Optional message to the donor", example = "We are a shelter home with 15 residents. This would help us greatly.")
    private String message;

    @Future(message = "Pickup scheduled time must be a future date/time")
    @Schema(description = "Preferred pickup time", example = "2024-12-31T12:00:00")
    private LocalDateTime pickupScheduledTime;
}
