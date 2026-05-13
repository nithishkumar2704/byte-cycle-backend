package com.bytecycle.fooddonor.service;

import com.bytecycle.fooddonor.dto.request.DonationRequest;
import com.bytecycle.fooddonor.dto.response.DonationResponse;
import com.bytecycle.fooddonor.entity.Donation;
import com.bytecycle.fooddonor.entity.User;
import com.bytecycle.fooddonor.enums.DonationStatus;
import com.bytecycle.fooddonor.exception.BadRequestException;
import com.bytecycle.fooddonor.exception.ResourceNotFoundException;
import com.bytecycle.fooddonor.exception.UnauthorizedAccessException;
import com.bytecycle.fooddonor.repository.DonationRepository;
import com.bytecycle.fooddonor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for all donation-related business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository userRepository;

    /**
     * Create a new food donation (Donor only).
     */
    @Transactional
    public DonationResponse createDonation(DonationRequest request, String donorEmail) {
        User donor = getUserByEmail(donorEmail);

        Donation donation = Donation.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .foodType(request.getFoodType())
                .quantity(request.getQuantity())
                .expiryTime(request.getExpiryTime())
                .pickupAddress(request.getPickupAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .status(DonationStatus.AVAILABLE)
                .isVegetarian(request.getIsVegetarian() != null ? request.getIsVegetarian() : false)
                .servesCount(request.getServesCount())
                .imageUrl(request.getImageUrl())
                .donor(donor)
                .build();

        Donation saved = donationRepository.save(donation);
        log.info("Donation created: id={} by donor={}", saved.getId(), donorEmail);
        return mapToResponse(saved);
    }

    /**
     * Update an existing donation (only by the owning donor).
     */
    @Transactional
    public DonationResponse updateDonation(Long donationId, DonationRequest request, String donorEmail) {
        User donor = getUserByEmail(donorEmail);
        Donation donation = donationRepository.findByIdAndDonor(donationId, donor)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Donation not found or you are not authorized to update it."));

        // Cannot update a completed or cancelled donation
        if (donation.getStatus() == DonationStatus.COMPLETED ||
                donation.getStatus() == DonationStatus.CANCELLED) {
            throw new BadRequestException("Cannot update a donation with status: " + donation.getStatus());
        }

        donation.setTitle(request.getTitle());
        donation.setDescription(request.getDescription());
        donation.setFoodType(request.getFoodType());
        donation.setQuantity(request.getQuantity());
        donation.setExpiryTime(request.getExpiryTime());
        donation.setPickupAddress(request.getPickupAddress());
        donation.setCity(request.getCity());
        donation.setState(request.getState());
        donation.setPincode(request.getPincode());
        donation.setServesCount(request.getServesCount());
        donation.setIsVegetarian(request.getIsVegetarian() != null ? request.getIsVegetarian() : false);
        if (request.getImageUrl() != null) donation.setImageUrl(request.getImageUrl());

        Donation updated = donationRepository.save(donation);
        log.info("Donation updated: id={}", donationId);
        return mapToResponse(updated);
    }

    /**
     * Delete a donation (only by the owning donor, only if AVAILABLE).
     */
    @Transactional
    public void deleteDonation(Long donationId, String donorEmail) {
        User donor = getUserByEmail(donorEmail);
        Donation donation = donationRepository.findByIdAndDonor(donationId, donor)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Donation not found or you are not authorized to delete it."));

        if (donation.getStatus() != DonationStatus.AVAILABLE) {
            throw new BadRequestException(
                    "Only AVAILABLE donations can be deleted. Current status: " + donation.getStatus());
        }

        donationRepository.delete(donation);
        log.info("Donation deleted: id={}", donationId);
    }

    /**
     * Mark a donation as COMPLETED (donor action).
     */
    @Transactional
    public DonationResponse markAsCompleted(Long donationId, String donorEmail) {
        User donor = getUserByEmail(donorEmail);
        Donation donation = donationRepository.findByIdAndDonor(donationId, donor)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Donation not found or you are not authorized to update it."));

        if (donation.getStatus() == DonationStatus.COMPLETED) {
            throw new BadRequestException("Donation is already marked as completed.");
        }
        if (donation.getStatus() == DonationStatus.CANCELLED) {
            throw new BadRequestException("Cannot complete a cancelled donation.");
        }

        donation.setStatus(DonationStatus.COMPLETED);
        Donation updated = donationRepository.save(donation);
        log.info("Donation marked as COMPLETED: id={}", donationId);
        return mapToResponse(updated);
    }

    /**
     * Cancel a donation (donor action).
     */
    @Transactional
    public DonationResponse cancelDonation(Long donationId, String donorEmail) {
        User donor = getUserByEmail(donorEmail);
        Donation donation = donationRepository.findByIdAndDonor(donationId, donor)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Donation not found or you are not authorized to cancel it."));

        if (donation.getStatus() == DonationStatus.COMPLETED ||
                donation.getStatus() == DonationStatus.CANCELLED) {
            throw new BadRequestException("Cannot cancel a donation with status: " + donation.getStatus());
        }

        donation.setStatus(DonationStatus.CANCELLED);
        Donation updated = donationRepository.save(donation);
        log.info("Donation cancelled: id={}", donationId);
        return mapToResponse(updated);
    }

    /**
     * Get all donations by the authenticated donor (paginated).
     */
    @Transactional(readOnly = true)
    public Page<DonationResponse> getMyDonations(String donorEmail, Pageable pageable) {
        User donor = getUserByEmail(donorEmail);
        return donationRepository.findByDonor(donor, pageable).map(this::mapToResponse);
    }

    /**
     * Get a single donation by ID (for the owning donor).
     */
    @Transactional(readOnly = true)
    public DonationResponse getDonationById(Long donationId, String donorEmail) {
        User donor = getUserByEmail(donorEmail);
        Donation donation = donationRepository.findByIdAndDonor(donationId, donor)
                .orElseThrow(() -> new ResourceNotFoundException("Donation", "id", donationId));
        return mapToResponse(donation);
    }

    // ======================== PUBLIC / RECEIVER METHODS ========================

    /**
     * Get all AVAILABLE donations (for receivers and public, paginated).
     */
    @Transactional(readOnly = true)
    public Page<DonationResponse> getAllAvailableDonations(Pageable pageable) {
        return donationRepository.findByStatus(DonationStatus.AVAILABLE, pageable).map(this::mapToResponse);
    }

    /**
     * Search donations by city and optional status (paginated).
     */
    @Transactional(readOnly = true)
    public Page<DonationResponse> searchDonations(String city, DonationStatus status, Pageable pageable) {
        return donationRepository.searchDonations(city, status, pageable).map(this::mapToResponse);
    }

    /**
     * Get any single donation by ID (for receivers to view details).
     */
    @Transactional(readOnly = true)
    public DonationResponse getPublicDonationById(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donation", "id", donationId));
        return mapToResponse(donation);
    }

    // ======================== PRIVATE HELPERS ========================

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    /**
     * Map Donation entity to DonationResponse DTO.
     */
    public DonationResponse mapToResponse(Donation donation) {
        return DonationResponse.builder()
                .id(donation.getId())
                .title(donation.getTitle())
                .description(donation.getDescription())
                .foodType(donation.getFoodType())
                .quantity(donation.getQuantity())
                .expiryTime(donation.getExpiryTime())
                .pickupAddress(donation.getPickupAddress())
                .city(donation.getCity())
                .state(donation.getState())
                .pincode(donation.getPincode())
                .status(donation.getStatus())
                .isVegetarian(donation.getIsVegetarian())
                .servesCount(donation.getServesCount())
                .imageUrl(donation.getImageUrl())
                .donorId(donation.getDonor().getId())
                .donorName(donation.getDonor().getFullName())
                .donorCity(donation.getDonor().getCity())
                .donorPhone(donation.getDonor().getPhone())
                .createdAt(donation.getCreatedAt())
                .updatedAt(donation.getUpdatedAt())
                .requestCount((long) donation.getRequests().size())
                .build();
    }
}
