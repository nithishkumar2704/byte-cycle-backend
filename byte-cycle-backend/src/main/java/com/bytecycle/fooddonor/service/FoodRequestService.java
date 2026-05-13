package com.bytecycle.fooddonor.service;

import com.bytecycle.fooddonor.dto.request.FoodRequestDto;
import com.bytecycle.fooddonor.dto.request.UpdateRequestStatusDto;
import com.bytecycle.fooddonor.dto.response.FoodRequestResponse;
import com.bytecycle.fooddonor.entity.Donation;
import com.bytecycle.fooddonor.entity.FoodRequest;
import com.bytecycle.fooddonor.entity.User;
import com.bytecycle.fooddonor.enums.DonationStatus;
import com.bytecycle.fooddonor.enums.RequestStatus;
import com.bytecycle.fooddonor.exception.BadRequestException;
import com.bytecycle.fooddonor.exception.ResourceNotFoundException;
import com.bytecycle.fooddonor.exception.UnauthorizedAccessException;
import com.bytecycle.fooddonor.repository.DonationRepository;
import com.bytecycle.fooddonor.repository.FoodRequestRepository;
import com.bytecycle.fooddonor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for food request operations by receivers and donor responses.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FoodRequestService {

    private final FoodRequestRepository foodRequestRepository;
    private final DonationRepository donationRepository;
    private final UserRepository userRepository;

    /**
     * Receiver creates a new food request for a donation.
     */
    @Transactional
    public FoodRequestResponse createRequest(Long donationId, FoodRequestDto dto, String receiverEmail) {
        User receiver = getUserByEmail(receiverEmail);
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donation", "id", donationId));

        // Donation must be AVAILABLE
        if (donation.getStatus() != DonationStatus.AVAILABLE) {
            throw new BadRequestException(
                    "This donation is not available. Current status: " + donation.getStatus());
        }

        // Prevent self-request (receiver cannot request their own donation if they're also a donor)
        if (donation.getDonor().getEmail().equals(receiverEmail)) {
            throw new BadRequestException("You cannot request your own donation.");
        }

        // Prevent duplicate active requests
        boolean alreadyRequested = foodRequestRepository.existsByReceiverAndDonationAndStatusIn(
                receiver, donation,
                List.of(RequestStatus.PENDING, RequestStatus.APPROVED)
        );
        if (alreadyRequested) {
            throw new BadRequestException("You already have an active request for this donation.");
        }

        FoodRequest foodRequest = FoodRequest.builder()
                .message(dto.getMessage())
                .pickupScheduledTime(dto.getPickupScheduledTime())
                .status(RequestStatus.PENDING)
                .receiver(receiver)
                .donation(donation)
                .build();

        FoodRequest saved = foodRequestRepository.save(foodRequest);

        // Update donation status to REQUESTED
        donation.setStatus(DonationStatus.REQUESTED);
        donationRepository.save(donation);

        log.info("Food request created: id={} by receiver={} for donation={}", saved.getId(), receiverEmail, donationId);
        return mapToResponse(saved);
    }

    /**
     * Receiver views their own request history (paginated).
     */
    @Transactional(readOnly = true)
    public Page<FoodRequestResponse> getMyRequests(String receiverEmail, Pageable pageable) {
        User receiver = getUserByEmail(receiverEmail);
        return foodRequestRepository.findByReceiver(receiver, pageable).map(this::mapToResponse);
    }

    /**
     * Receiver cancels their own pending request.
     */
    @Transactional
    public FoodRequestResponse cancelRequest(Long requestId, String receiverEmail) {
        User receiver = getUserByEmail(receiverEmail);
        FoodRequest request = foodRequestRepository.findByIdAndReceiver(requestId, receiver)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Request not found or you are not authorized to cancel it."));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException(
                    "Only PENDING requests can be cancelled. Current status: " + request.getStatus());
        }

        request.setStatus(RequestStatus.CANCELLED);
        FoodRequest updated = foodRequestRepository.save(request);

        // Revert donation status back to AVAILABLE
        Donation donation = request.getDonation();
        donation.setStatus(DonationStatus.AVAILABLE);
        donationRepository.save(donation);

        log.info("Request cancelled: id={}", requestId);
        return mapToResponse(updated);
    }

    // ======================== DONOR METHODS ========================

    /**
     * Donor views all requests for a specific donation they own.
     */
    @Transactional(readOnly = true)
    public Page<FoodRequestResponse> getRequestsForDonation(Long donationId, String donorEmail, Pageable pageable) {
        User donor = getUserByEmail(donorEmail);
        Donation donation = donationRepository.findByIdAndDonor(donationId, donor)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Donation not found or you are not authorized to view its requests."));

        return foodRequestRepository.findByDonation(donation, pageable).map(this::mapToResponse);
    }

    /**
     * Donor updates the status of a request (APPROVE / REJECT / COMPLETE).
     */
    @Transactional
    public FoodRequestResponse updateRequestStatus(Long requestId, UpdateRequestStatusDto dto, String donorEmail) {
        User donor = getUserByEmail(donorEmail);

        FoodRequest request = foodRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));

        // Ensure the donor owns the donation
        if (!request.getDonation().getDonor().getEmail().equals(donorEmail)) {
            throw new UnauthorizedAccessException("You are not authorized to update this request.");
        }

        RequestStatus newStatus = dto.getStatus();

        // Validate allowed transitions
        validateStatusTransition(request.getStatus(), newStatus);

        request.setStatus(newStatus);
        if (dto.getDonorNotes() != null) request.setDonorNotes(dto.getDonorNotes());

        // Update donation status based on request outcome
        Donation donation = request.getDonation();
        if (newStatus == RequestStatus.APPROVED) {
            donation.setStatus(DonationStatus.REQUESTED);
        } else if (newStatus == RequestStatus.REJECTED || newStatus == RequestStatus.CANCELLED) {
            donation.setStatus(DonationStatus.AVAILABLE);
        } else if (newStatus == RequestStatus.COMPLETED) {
            donation.setStatus(DonationStatus.COMPLETED);
        }
        donationRepository.save(donation);

        FoodRequest updated = foodRequestRepository.save(request);
        log.info("Request status updated: id={} -> {}", requestId, newStatus);
        return mapToResponse(updated);
    }

    // ======================== PRIVATE HELPERS ========================

    private void validateStatusTransition(RequestStatus current, RequestStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == RequestStatus.APPROVED ||
                    next == RequestStatus.REJECTED ||
                    next == RequestStatus.CANCELLED;
            case APPROVED -> next == RequestStatus.COMPLETED ||
                    next == RequestStatus.CANCELLED;
            default -> false;
        };

        if (!valid) {
            throw new BadRequestException(
                    String.format("Invalid status transition: %s -> %s", current, next));
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    /**
     * Map FoodRequest entity to FoodRequestResponse DTO.
     */
    private FoodRequestResponse mapToResponse(FoodRequest request) {
        return FoodRequestResponse.builder()
                .id(request.getId())
                .message(request.getMessage())
                .status(request.getStatus())
                .pickupScheduledTime(request.getPickupScheduledTime())
                .donorNotes(request.getDonorNotes())
                .receiverId(request.getReceiver().getId())
                .receiverName(request.getReceiver().getFullName())
                .receiverEmail(request.getReceiver().getEmail())
                .receiverPhone(request.getReceiver().getPhone())
                .donationId(request.getDonation().getId())
                .donationTitle(request.getDonation().getTitle())
                .donationAddress(request.getDonation().getPickupAddress())
                .donorName(request.getDonation().getDonor().getFullName())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
