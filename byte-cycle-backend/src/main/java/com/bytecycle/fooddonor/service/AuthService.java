package com.bytecycle.fooddonor.service;

import com.bytecycle.fooddonor.dto.request.LoginRequest;
import com.bytecycle.fooddonor.dto.request.RegisterRequest;
import com.bytecycle.fooddonor.dto.response.AuthResponse;
import com.bytecycle.fooddonor.dto.response.UserProfileResponse;
import com.bytecycle.fooddonor.entity.User;
import com.bytecycle.fooddonor.exception.DuplicateResourceException;
import com.bytecycle.fooddonor.exception.ResourceNotFoundException;
import com.bytecycle.fooddonor.repository.FoodRequestRepository;
import com.bytecycle.fooddonor.repository.DonationRepository;
import com.bytecycle.fooddonor.repository.UserRepository;
import com.bytecycle.fooddonor.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations: registration and login.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final DonationRepository donationRepository;
    private final FoodRequestRepository foodRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * Register a new user (donor or receiver).
     *
     * @param request registration details
     * @return AuthResponse with JWT token and user info
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email address is already registered: " + request.getEmail());
        }

        // Check for duplicate phone
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Phone number is already registered: " + request.getPhone());
        }

        // Build and save user entity
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .role(request.getRole())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: id={}, role={}", savedUser.getId(), savedUser.getRole());

        // Generate JWT token
        String token = generateTokenForUser(savedUser);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .message("Registration successful! Welcome to Byte Cycle.")
                .build();
    }

    /**
     * Authenticate an existing user and return a JWT token.
     *
     * @param request login credentials
     * @return AuthResponse with JWT token and user info
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Authenticate via Spring Security (throws BadCredentialsException if invalid)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Load the full user entity
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        String token = generateTokenForUser(user);
        log.info("Login successful for user: id={}, role={}", user.getId(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Login successful! Welcome back, " + user.getFullName() + ".")
                .build();
    }

    /**
     * Get the profile of the currently authenticated user.
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        long totalDonations = donationRepository.countByDonor(user);
        long totalRequests = foodRequestRepository.countByReceiver(user);

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .pincode(user.getPincode())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .totalDonations(totalDonations)
                .totalRequests(totalRequests)
                .build();
    }

    // ---- Private helpers ----

    private String generateTokenForUser(User user) {
        // Build Spring Security UserDetails from our User entity
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
        return jwtUtils.generateToken(userDetails);
    }
}
