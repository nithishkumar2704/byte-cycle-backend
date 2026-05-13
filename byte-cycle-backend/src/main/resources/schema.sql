-- ============================================================
-- Byte Cycle - Food Donor Service Platform
-- PostgreSQL Database Schema
-- ============================================================

-- Create database (run this separately as a superuser)
-- CREATE DATABASE bytecycle_db;
-- \c bytecycle_db;

-- ============================================================
-- TABLE: users
-- Stores both donors and receivers
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL       PRIMARY KEY,
    full_name   VARCHAR(100)    NOT NULL,
    email       VARCHAR(150)    NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,
    phone       VARCHAR(15)     UNIQUE,
    address     VARCHAR(255),
    city        VARCHAR(100),
    state       VARCHAR(100),
    pincode     VARCHAR(10),
    role        VARCHAR(20)     NOT NULL CHECK (role IN ('DONOR', 'RECEIVER')),
    is_active   BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_users_email  ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role   ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_city   ON users(city);

-- ============================================================
-- TABLE: donations
-- Food donation listings created by donors
-- ============================================================
CREATE TABLE IF NOT EXISTS donations (
    id              BIGSERIAL       PRIMARY KEY,
    title           VARCHAR(150)    NOT NULL,
    description     TEXT,
    food_type       VARCHAR(100)    NOT NULL,
    quantity        VARCHAR(50)     NOT NULL,
    expiry_time     TIMESTAMP,
    pickup_address  VARCHAR(255)    NOT NULL,
    city            VARCHAR(100)    NOT NULL,
    state           VARCHAR(100),
    pincode         VARCHAR(10),
    status          VARCHAR(20)     NOT NULL DEFAULT 'AVAILABLE'
                        CHECK (status IN ('AVAILABLE', 'REQUESTED', 'COMPLETED', 'CANCELLED')),
    is_vegetarian   BOOLEAN         NOT NULL DEFAULT FALSE,
    serves_count    INT,
    image_url       VARCHAR(500),
    donor_id        BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_donations_city       ON donations(city);
CREATE INDEX IF NOT EXISTS idx_donations_status     ON donations(status);
CREATE INDEX IF NOT EXISTS idx_donations_donor_id   ON donations(donor_id);
CREATE INDEX IF NOT EXISTS idx_donations_created_at ON donations(created_at DESC);

-- ============================================================
-- TABLE: requests
-- Food requests submitted by receivers for donations
-- ============================================================
CREATE TABLE IF NOT EXISTS requests (
    id                      BIGSERIAL   PRIMARY KEY,
    message                 TEXT,
    status                  VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                                CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED')),
    pickup_scheduled_time   TIMESTAMP,
    donor_notes             VARCHAR(500),
    receiver_id             BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    donation_id             BIGINT      NOT NULL REFERENCES donations(id) ON DELETE CASCADE,
    created_at              TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_requests_receiver_id ON requests(receiver_id);
CREATE INDEX IF NOT EXISTS idx_requests_donation_id ON requests(donation_id);
CREATE INDEX IF NOT EXISTS idx_requests_status      ON requests(status);

-- ============================================================
-- Automatic updated_at trigger function
-- ============================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_donations_updated_at
    BEFORE UPDATE ON donations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_requests_updated_at
    BEFORE UPDATE ON requests
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- Sample seed data (optional - for development/testing)
-- ============================================================

-- Sample donor (password: Password@123)
-- INSERT INTO users (full_name, email, password, phone, city, state, pincode, role)
-- VALUES ('Rajesh Kumar', 'rajesh@example.com',
--         '$2a$12$hashed_password_here', '9876543210',
--         'Chennai', 'Tamil Nadu', '600001', 'DONOR');

-- Sample receiver (password: Password@123)
-- INSERT INTO users (full_name, email, password, phone, city, state, pincode, role)
-- VALUES ('Priya Sharma', 'priya@example.com',
--         '$2a$12$hashed_password_here', '9123456789',
--         'Chennai', 'Tamil Nadu', '600002', 'RECEIVER');
