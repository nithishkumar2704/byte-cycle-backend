package com.bytecycle.fooddonor.enums;

/**
 * Enum representing the lifecycle status of a food donation.
 *
 * AVAILABLE   - Donation is live and visible to receivers
 * REQUESTED   - A receiver has placed a request for the donation
 * COMPLETED   - Donation has been successfully handed over
 * CANCELLED   - Donor cancelled the donation listing
 */
public enum DonationStatus {
    AVAILABLE,
    REQUESTED,
    COMPLETED,
    CANCELLED
}
