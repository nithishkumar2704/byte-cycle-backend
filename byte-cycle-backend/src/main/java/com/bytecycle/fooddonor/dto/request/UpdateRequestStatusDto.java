package com.bytecycle.fooddonor.dto.request;

import com.bytecycle.fooddonor.enums.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for donor to update the status of a food request (approve/reject).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update request status payload (for donors)")
public class UpdateRequestStatusDto {

    @NotNull(message = "Status is required")
    @Schema(description = "New status for the request", example = "APPROVED",
            allowableValues = {"APPROVED", "REJECTED", "COMPLETED"})
    private RequestStatus status;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Schema(description = "Optional note from donor to receiver", example = "Please come by 5 PM. Call before arriving.")
    private String donorNotes;
}
