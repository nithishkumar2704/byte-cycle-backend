# Byte Cycle — Food Donor Service Platform Backend

> Connecting food donors with recipients to eliminate urban food waste.

[![Java](https://img.shields.io/badge/Java-17-orange?logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/Auth-JWT-yellow)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/API%20Docs-Swagger%20UI-green)](http://localhost:8080/api/swagger-ui.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-purple.svg)](https://opensource.org/licenses/MIT)

---

## Project Overview

**Byte Cycle** is a production-grade RESTful backend service that bridges the gap between food donors and recipients. Donors can list surplus food, and receivers can browse, search, and request donations — all secured with JWT-based authentication and role-based access control.

---

##  Features

###  Donor
- Register & login with JWT
- Create, update, and delete food donation listings
- View and manage all their donations
- Mark donations as completed or cancel them
- View and respond to receiver requests (approve / reject / complete)

###  Receiver
- Register & login with JWT
- Browse all available food donations
- Search donations by city / location
- Place food requests with optional messages
- View full request history
- Cancel pending requests

###  General / Public
- Public donation browsing without login
- Location-based search
- Full donation status lifecycle management
- Input validation & structured error responses
- Swagger UI for live API testing

---

##  Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.0 |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| ORM | Spring Data JPA + Hibernate |
| Database | PostgreSQL 15 |
| Build Tool | Apache Maven |
| API Docs | SpringDoc OpenAPI 3 / Swagger UI |
| Utilities | Lombok |
| Password Encryption | BCrypt (strength 12) |

---

##  Project Structure

```
byte-cycle-backend/
├── src/
│   ├── main/
│   │   ├── java/com/bytecycle/fooddonor/
│   │   │   ├── ByteCycleApplication.java       # Main entry point
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java          # Spring Security + CORS
│   │   │   │   └── SwaggerConfig.java           # OpenAPI / Swagger setup
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java          # /api/auth/**
│   │   │   │   ├── DonorController.java         # /api/donor/**
│   │   │   │   ├── ReceiverController.java      # /api/receiver/**
│   │   │   │   └── PublicDonationController.java# /api/donations/public/**
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── DonationService.java
│   │   │   │   └── FoodRequestService.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── DonationRepository.java
│   │   │   │   └── FoodRequestRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   ├── Donation.java
│   │   │   │   └── FoodRequest.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── DonationRequest.java
│   │   │   │   │   ├── FoodRequestDto.java
│   │   │   │   │   └── UpdateRequestStatusDto.java
│   │   │   │   └── response/
│   │   │   │       ├── ApiResponse.java
│   │   │   │       ├── AuthResponse.java
│   │   │   │       ├── DonationResponse.java
│   │   │   │       ├── FoodRequestResponse.java
│   │   │   │       └── UserProfileResponse.java
│   │   │   ├── enums/
│   │   │   │   ├── UserRole.java
│   │   │   │   ├── DonationStatus.java
│   │   │   │   └── RequestStatus.java
│   │   │   ├── security/
│   │   │   │   ├── jwt/
│   │   │   │   │   ├── JwtUtils.java
│   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   └── JwtAuthEntryPoint.java
│   │   │   │   └── service/
│   │   │   │       └── UserDetailsServiceImpl.java
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── ResourceNotFoundException.java
│   │   │       ├── DuplicateResourceException.java
│   │   │       ├── BadRequestException.java
│   │   │       └── UnauthorizedAccessException.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── schema.sql
│   └── test/
│       ├── java/.../ByteCycleApplicationTests.java
│       └── resources/application-test.properties
├── pom.xml
├── .gitignore
└── README.md
```

---

##  Installation & Setup

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 13+

### 1. Clone the Repository

```bash
git clone https://github.com/nithishkumar2704/byte-cycle-backend.git
cd byte-cycle-backend
```

### 2. PostgreSQL Database Setup

```sql
-- Connect to PostgreSQL as superuser
psql -U postgres

-- Create the database
CREATE DATABASE bytecycle_db;

-- Verify creation
\l

-- Exit
\q
```

Then run the schema:
```bash
psql -U postgres -d bytecycle_db -f src/main/resources/schema.sql
```

### 3. Configure Application

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bytecycle_db
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password

app.jwt.secret=YourSuperSecretJWTKeyMustBeAtLeast256BitsLong!
app.jwt.expiration=86400000
```

### 4. Build & Run

```bash
# Clean and build
mvn clean install -DskipTests

# Run the application
mvn spring-boot:run
```

The server starts at: `http://localhost:8080`

---

##  API Endpoints

### Base URL: `http://localhost:8080/api`

###  Authentication (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/register` | Register as DONOR or RECEIVER |
| `POST` | `/auth/login` | Login and receive JWT token |
| `GET`  | `/auth/me` | Get current user profile (auth required) |

**Register Request Body:**
```json
{
  "fullName": "Nithish Kumar",
  "email": "nithish@example.com",
  "password": "Password@123",
  "phone": "9876543210",
  "city": "Chennai",
  "state": "Tamil Nadu",
  "pincode": "600001",
  "role": "DONOR"
}
```

**Login Response:**
```json
{
  "success": true,
  "message": "Login successful.",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "fullName": "Nithish Kumar",
    "email": "nithish@example.com",
    "role": "DONOR"
  }
}
```

---

###  Donor Endpoints (Requires `ROLE_DONOR` + Bearer Token)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST`  | `/donor/donations` | Create a donation |
| `GET`   | `/donor/donations` | List my donations |
| `GET`   | `/donor/donations/{id}` | Get donation by ID |
| `PUT`   | `/donor/donations/{id}` | Update a donation |
| `DELETE`| `/donor/donations/{id}` | Delete a donation |
| `PATCH` | `/donor/donations/{id}/complete` | Mark as completed |
| `PATCH` | `/donor/donations/{id}/cancel` | Cancel a donation |
| `GET`   | `/donor/donations/{id}/requests` | View requests for a donation |
| `PATCH` | `/donor/requests/{id}/status` | Approve/Reject/Complete a request |

**Create Donation Request Body:**
```json
{
  "title": "Fresh Cooked Rice and Dal",
  "description": "Freshly cooked rice and dal, made this morning.",
  "foodType": "Cooked Meal",
  "quantity": "5 kg",
  "expiryTime": "2025-12-31T18:00:00",
  "pickupAddress": "45 Gandhi Nagar, Anna Salai",
  "city": "Chennai",
  "state": "Tamil Nadu",
  "pincode": "600001",
  "isVegetarian": true,
  "servesCount": 10
}
```

---

###  Receiver Endpoints (Requires `ROLE_RECEIVER` + Bearer Token)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET`   | `/receiver/donations` | Browse available donations |
| `GET`   | `/receiver/donations/{id}` | View donation details |
| `GET`   | `/receiver/donations/search` | Search by city/status |
| `POST`  | `/receiver/donations/{id}/request` | Request a donation |
| `GET`   | `/receiver/requests` | View my request history |
| `PATCH` | `/receiver/requests/{id}/cancel` | Cancel a request |

**Search Donations:**
```
GET /receiver/donations/search?city=Chennai&status=AVAILABLE&page=0&size=10
```

---

###  Public Endpoints (No Authentication)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/donations/public` | Browse all available donations |
| `GET` | `/donations/public/{id}` | View a specific donation |
| `GET` | `/donations/public/search` | Search by city |

---

##  Database Schema

```
users ──< donations ──< requests >── users
  (donor)                              (receiver)
```

- **users**: id, full_name, email, password, phone, address, city, state, pincode, role, is_active
- **donations**: id, title, description, food_type, quantity, expiry_time, pickup_address, city, status, is_vegetarian, serves_count, donor_id
- **requests**: id, message, status, pickup_scheduled_time, donor_notes, receiver_id, donation_id

---

##  Donation & Request Status Flow

**Donation Status:**
```
AVAILABLE → REQUESTED → COMPLETED
           ↓
        CANCELLED
```

**Request Status:**
```
PENDING → APPROVED → COMPLETED
        ↓
      REJECTED / CANCELLED
```

---

##  Authentication

All protected endpoints require the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

JWT tokens expire after **24 hours** (configurable in `application.properties`).

---

##  Swagger UI

Access the interactive API documentation at:

```
http://localhost:8080/api/swagger-ui.html
```

- Click **Authorize** button
- Enter: `Bearer <your_jwt_token>`
- Test all endpoints directly from the browser

---

##  Maven Commands

```bash
# Clean build (skip tests)
mvn clean install -DskipTests

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Package as JAR
mvn clean package -DskipTests

# Run the JAR
java -jar target/byte-cycle-backend-1.0.0.jar
```

---

##  GitHub Push Commands

```bash
git init
git add .
git commit -m "Initial commit - Byte Cycle Backend"
git branch -M main
git remote add origin https://github.com/nithishkumar2704/byte-cycle-backend.git
git push -u origin main
```

---

##  Future Enhancements

- [ ] Email notifications on request approval/rejection
- [ ] Real-time notifications using WebSocket
- [ ] Google Maps integration for pickup location
- [ ] Donor rating and review system
- [ ] Admin dashboard with analytics
- [ ] Food category filtering with tags
- [ ] Mobile push notifications
- [ ] Docker + docker-compose setup
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Rate limiting per user
- [ ] Soft delete for donations
- [ ] Export reports (CSV/PDF)

---

##  Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add your feature"`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

##  License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

---

<div align="center">
  <strong>Built with ❤️ to reduce food waste — one byte at a time.</strong>
</div>
