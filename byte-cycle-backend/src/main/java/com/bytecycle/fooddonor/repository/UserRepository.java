package com.bytecycle.fooddonor.repository;

import com.bytecycle.fooddonor.entity.User;
import com.bytecycle.fooddonor.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides database operations for user management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their email address.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if an email address is already registered.
     */
    boolean existsByEmail(String email);

    /**
     * Check if a phone number is already registered.
     */
    boolean existsByPhone(String phone);

    /**
     * Count users by role.
     */
    long countByRole(UserRole role);
}
