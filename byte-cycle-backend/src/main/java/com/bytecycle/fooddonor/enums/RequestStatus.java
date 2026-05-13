package com.bytecycle.fooddonor.enums;

/**
 * Enum representing the status of a food request made by a receiver.
 *
 * PENDING   - Request submitted, waiting for donor action
 * APPROVED  - Donor approved the request
 * REJECTED  - Donor rejected the request
 * CANCELLED - Receiver cancelled their request
 * COMPLETED - Food has been received successfully
 */
public enum RequestStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED,
    COMPLETED
}
