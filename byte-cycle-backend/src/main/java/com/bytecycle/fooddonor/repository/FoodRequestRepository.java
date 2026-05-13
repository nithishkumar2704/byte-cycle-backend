package com.bytecycle.fooddonor.repository;

import com.bytecycle.fooddonor.entity.Donation;
import com.bytecycle.fooddonor.entity.FoodRequest;
import com.bytecycle.fooddonor.entity.User;
import com.bytecycle.fooddonor.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for FoodRequest entity.
 */
@Repository
public interface FoodRequestRepository extends JpaRepository<FoodRequest, Long> {

    /**
     * Find all requests made by a specific receiver.
     */
    Page<FoodRequest> findByReceiver(User receiver, Pageable pageable);

    /**
     * Find all requests for a specific donation.
     */
    Page<FoodRequest> findByDonation(Donation donation, Pageable pageable);

    /**
     * Find request by ID and receiver (ownership check).
     */
    Optional<FoodRequest> findByIdAndReceiver(Long id, User receiver);

    /**
     * Check if a receiver already has an active/pending request for a donation.
     */
    boolean existsByReceiverAndDonationAndStatusIn(
            User receiver,
            Donation donation,
            java.util.List<RequestStatus> statuses
    );

    /**
     * Count requests by status.
     */
    long countByStatus(RequestStatus status);

    /**
     * Count requests by receiver.
     */
    long countByReceiver(User receiver);
}
