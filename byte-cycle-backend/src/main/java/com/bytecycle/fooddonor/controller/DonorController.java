package com.bytecycle.fooddonor.controller;

import com.bytecycle.fooddonor.dto.request.DonationRequest;
import com.bytecycle.fooddonor.dto.request.UpdateRequestStatusDto;
import com.bytecycle.fooddonor.dto.response.ApiResponse;
import com.bytecycle.fooddonor.dto.response.DonationResponse;
import com.bytecycle.fooddonor.dto.response.FoodRequestResponse;
import com.bytecycle.fooddonor.service.DonationService;
import com.bytecycle.fooddonor.service.FoodRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for DONOR-specific operations.
 * All endpoints require ROLE_DONOR.
 */
@RestController
@RequestMapping("/donor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DONOR')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Donor", description = "Donation management APIs for donors")
public class DonorController {

    private final DonationService donationService;
    private final FoodRequestService foodRequestService;

    // ======================== DONATION CRUD ========================

    /**
     * POST /api/donor/donations
     * Create a new food donation listing.
     */
    @PostMapping("/donations")
    @Operation(summary = "Create a food donation", description = "Donor creates a new food donation listing.")
    public ResponseEntity<ApiResponse<DonationResponse>> createDonation(
            @Valid @RequestBody DonationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        DonationResponse response = donationService.createDonation(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Donation created successfully.", response));
    }

    /**
     * PUT /api/donor/donations/{id}
     * Update an existing donation (only by the owner).
     */
    @PutMapping("/donations/{id}")
    @Operation(summary = "Update a donation", description = "Donor updates their own food donation.")
    public ResponseEntity<ApiResponse<DonationResponse>> updateDonation(
            @PathVariable Long id,
            @Valid @RequestBody DonationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        DonationResponse response = donationService.updateDonation(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Donation updated successfully.", response));
    }

    /**
     * DELETE /api/donor/donations/{id}
     * Delete an AVAILABLE donation (only by the owner).
     */
    @DeleteMapping("/donations/{id}")
    @Operation(summary = "Delete a donation", description = "Donor deletes their own AVAILABLE donation.")
    public ResponseEntity<ApiResponse<Void>> deleteDonation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        donationService.deleteDonation(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Donation deleted successfully."));
    }

    /**
     * GET /api/donor/donations
     * Get all donations created by the logged-in donor (paginated).
     */
    @GetMapping("/donations")
    @Operation(summary = "Get my donations", description = "Returns all donations created by the authenticated donor.")
    public ResponseEntity<ApiResponse<Page<DonationResponse>>> getMyDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DonationResponse> donations = donationService.getMyDonations(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Donations fetched successfully.", donations));
    }

    /**
     * GET /api/donor/donations/{id}
     * Get a specific donation by ID (owned by the donor).
     */
    @GetMapping("/donations/{id}")
    @Operation(summary = "Get donation by ID", description = "Get details of a specific owned donation.")
    public ResponseEntity<ApiResponse<DonationResponse>> getDonationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        DonationResponse response = donationService.getDonationById(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Donation fetched successfully.", response));
    }

    /**
     * PATCH /api/donor/donations/{id}/complete
     * Mark a donation as COMPLETED.
     */
    @PatchMapping("/donations/{id}/complete")
    @Operation(summary = "Mark donation as completed", description = "Donor marks a donation as COMPLETED.")
    public ResponseEntity<ApiResponse<DonationResponse>> markAsCompleted(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        DonationResponse response = donationService.markAsCompleted(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Donation marked as completed.", response));
    }

    /**
     * PATCH /api/donor/donations/{id}/cancel
     * Cancel a donation.
     */
    @PatchMapping("/donations/{id}/cancel")
    @Operation(summary = "Cancel a donation", description = "Donor cancels their food donation.")
    public ResponseEntity<ApiResponse<DonationResponse>> cancelDonation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        DonationResponse response = donationService.cancelDonation(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Donation cancelled.", response));
    }

    // ======================== REQUEST MANAGEMENT ========================

    /**
     * GET /api/donor/donations/{donationId}/requests
     * View all requests for a specific donation.
     */
    @GetMapping("/donations/{donationId}/requests")
    @Operation(summary = "Get requests for a donation", description = "Donor views all requests for a specific donation.")
    public ResponseEntity<ApiResponse<Page<FoodRequestResponse>>> getRequestsForDonation(
            @PathVariable Long donationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FoodRequestResponse> requests =
                foodRequestService.getRequestsForDonation(donationId, userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Requests fetched successfully.", requests));
    }

    /**
     * PATCH /api/donor/requests/{requestId}/status
     * Approve, reject, or complete a receiver's food request.
     */
    @PatchMapping("/requests/{requestId}/status")
    @Operation(summary = "Update request status", description = "Donor approves, rejects, or completes a receiver's request.")
    public ResponseEntity<ApiResponse<FoodRequestResponse>> updateRequestStatus(
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateRequestStatusDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        FoodRequestResponse response =
                foodRequestService.updateRequestStatus(requestId, dto, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Request status updated successfully.", response));
    }
}
