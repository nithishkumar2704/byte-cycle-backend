package com.bytecycle.fooddonor.controller;

import com.bytecycle.fooddonor.dto.request.FoodRequestDto;
import com.bytecycle.fooddonor.dto.response.ApiResponse;
import com.bytecycle.fooddonor.dto.response.DonationResponse;
import com.bytecycle.fooddonor.dto.response.FoodRequestResponse;
import com.bytecycle.fooddonor.enums.DonationStatus;
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
 * REST Controller for RECEIVER-specific operations.
 * All endpoints require ROLE_RECEIVER.
 */
@RestController
@RequestMapping("/receiver")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECEIVER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Receiver", description = "Donation browsing and food request APIs for receivers")
public class ReceiverController {

    private final DonationService donationService;
    private final FoodRequestService foodRequestService;

    // ======================== BROWSE DONATIONS ========================

    /**
     * GET /api/receiver/donations
     * View all AVAILABLE donations (paginated).
     */
    @GetMapping("/donations")
    @Operation(summary = "Browse available donations", description = "Receiver browses all currently available food donations.")
    public ResponseEntity<ApiResponse<Page<DonationResponse>>> getAllAvailableDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DonationResponse> donations = donationService.getAllAvailableDonations(pageable);
        return ResponseEntity.ok(ApiResponse.success("Available donations fetched.", donations));
    }

    /**
     * GET /api/receiver/donations/{id}
     * View details of a specific donation.
     */
    @GetMapping("/donations/{id}")
    @Operation(summary = "Get donation details", description = "Receiver views the full details of a specific donation.")
    public ResponseEntity<ApiResponse<DonationResponse>> getDonationById(@PathVariable Long id) {
        DonationResponse response = donationService.getPublicDonationById(id);
        return ResponseEntity.ok(ApiResponse.success("Donation fetched successfully.", response));
    }

    /**
     * GET /api/receiver/donations/search
     * Search donations by city and optional status.
     */
    @GetMapping("/donations/search")
    @Operation(
            summary = "Search donations by location",
            description = "Receiver searches donations by city name with an optional status filter."
    )
    public ResponseEntity<ApiResponse<Page<DonationResponse>>> searchDonations(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) DonationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DonationResponse> donations = donationService.searchDonations(city, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results fetched.", donations));
    }

    // ======================== FOOD REQUESTS ========================

    /**
     * POST /api/receiver/donations/{donationId}/request
     * Request a specific food donation.
     */
    @PostMapping("/donations/{donationId}/request")
    @Operation(summary = "Request a donation", description = "Receiver places a request for a specific food donation.")
    public ResponseEntity<ApiResponse<FoodRequestResponse>> requestDonation(
            @PathVariable Long donationId,
            @Valid @RequestBody FoodRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        FoodRequestResponse response =
                foodRequestService.createRequest(donationId, dto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Food request submitted successfully.", response));
    }

    /**
     * GET /api/receiver/requests
     * View the authenticated receiver's request history.
     */
    @GetMapping("/requests")
    @Operation(summary = "View my request history", description = "Receiver views all food requests they have made.")
    public ResponseEntity<ApiResponse<Page<FoodRequestResponse>>> getMyRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<FoodRequestResponse> requests =
                foodRequestService.getMyRequests(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Request history fetched.", requests));
    }

    /**
     * PATCH /api/receiver/requests/{requestId}/cancel
     * Cancel a pending food request.
     */
    @PatchMapping("/requests/{requestId}/cancel")
    @Operation(summary = "Cancel a request", description = "Receiver cancels their own PENDING food request.")
    public ResponseEntity<ApiResponse<FoodRequestResponse>> cancelRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetails userDetails) {

        FoodRequestResponse response =
                foodRequestService.cancelRequest(requestId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Request cancelled successfully.", response));
    }
}
