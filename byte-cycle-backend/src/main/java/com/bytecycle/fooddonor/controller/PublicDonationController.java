package com.bytecycle.fooddonor.controller;

import com.bytecycle.fooddonor.dto.response.ApiResponse;
import com.bytecycle.fooddonor.dto.response.DonationResponse;
import com.bytecycle.fooddonor.enums.DonationStatus;
import com.bytecycle.fooddonor.service.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public REST Controller for browsing food donations without authentication.
 * Allows anyone to view available donations.
 */
@RestController
@RequestMapping("/donations/public")
@RequiredArgsConstructor
@Tag(name = "Public Donations", description = "Public donation browsing APIs (no authentication required)")
public class PublicDonationController {

    private final DonationService donationService;

    /**
     * GET /api/donations/public
     * Browse all available donations publicly.
     */
    @GetMapping
    @Operation(summary = "Browse all available donations", description = "Public endpoint to view all available food donations.")
    public ResponseEntity<ApiResponse<Page<DonationResponse>>> getAllAvailableDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DonationResponse> donations = donationService.getAllAvailableDonations(pageable);
        return ResponseEntity.ok(ApiResponse.success("Available donations fetched.", donations));
    }

    /**
     * GET /api/donations/public/{id}
     * View details of a specific donation publicly.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get donation details (public)", description = "View full details of a specific donation without login.")
    public ResponseEntity<ApiResponse<DonationResponse>> getDonationById(@PathVariable Long id) {
        DonationResponse response = donationService.getPublicDonationById(id);
        return ResponseEntity.ok(ApiResponse.success("Donation fetched.", response));
    }

    /**
     * GET /api/donations/public/search
     * Search donations by city (public, no auth required).
     */
    @GetMapping("/search")
    @Operation(summary = "Search donations by location (public)", description = "Search available donations by city name.")
    public ResponseEntity<ApiResponse<Page<DonationResponse>>> searchByLocation(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) DonationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DonationResponse> donations = donationService.searchDonations(city, status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results fetched.", donations));
    }
}
