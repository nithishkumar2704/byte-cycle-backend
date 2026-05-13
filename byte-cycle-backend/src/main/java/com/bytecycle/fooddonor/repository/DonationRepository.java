package com.bytecycle.fooddonor.repository;

import com.bytecycle.fooddonor.entity.Donation;
import com.bytecycle.fooddonor.entity.User;
import com.bytecycle.fooddonor.enums.DonationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Donation entity.
 * Provides database operations for donation management.
 */
@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    /**
     * Find all donations by a specific donor.
     */
    Page<Donation> findByDonor(User donor, Pageable pageable);

    /**
     * Find all donations by a specific donor's ID.
     */
    List<Donation> findByDonorId(Long donorId);

    /**
     * Find all donations with a specific status.
     */
    Page<Donation> findByStatus(DonationStatus status, Pageable pageable);

    /**
     * Find all available donations in a specific city (case-insensitive).
     */
    Page<Donation> findByCityIgnoreCaseAndStatus(String city, DonationStatus status, Pageable pageable);

    /**
     * Search donations by city, optionally filtered by status.
     */
    @Query("SELECT d FROM Donation d WHERE " +
            "(:city IS NULL OR LOWER(d.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:status IS NULL OR d.status = :status) " +
            "ORDER BY d.createdAt DESC")
    Page<Donation> searchDonations(
            @Param("city") String city,
            @Param("status") DonationStatus status,
            Pageable pageable
    );

    /**
     * Find a donation by ID and donor.
     */
    Optional<Donation> findByIdAndDonor(Long id, User donor);

    /**
     * Count donations by status.
     */
    long countByStatus(DonationStatus status);

    /**
     * Count donations by donor.
     */
    long countByDonor(User donor);
}
