# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot backend application for a tourist luggage storage service. The platform connects three types of users:
1. **Customers** - Book luggage storage at various locations
2. **Hosts** - Provide storage locations and manage bookings
3. **Admins (Devs)** - Ensure the application runs smoothly

**Tech Stack:**
- Spring Boot 3.5.6
- Java 25
- PostgreSQL 14
- Spring Data JPA
- Project Lombok
- Maven
- Jakarta Validation

## Development Commands

### Database Setup
Start the PostgreSQL database using Docker Compose:
```bash
docker-compose up -d
```

Database credentials (defined in docker-compose.yml):
- Database: `luggage-backend`
- User: `luggo`
- Password: `luggo`
- Port: `5432`

### Building and Running

Build the project:
```bash
./mvnw clean install
```

Run the application:
```bash
./mvnw spring-boot:run
```

The application runs on `http://localhost:8080`

### Testing

Run all tests:
```bash
./mvnw test
```

Run a specific test class:
```bash
./mvnw test -Dtest=ClassName
```

Run a specific test method:
```bash
./mvnw test -Dtest=ClassName#methodName
```

### Development Tools

Spring Boot DevTools is enabled for automatic restarts during development.

## Architecture

### Layered Architecture

The application follows a standard Spring Boot layered architecture:

1. **Controller Layer** (`com.dani.luggagebackend.Controller`)
   - REST endpoints
   - Request/response handling
   - All controllers use `@CrossOrigin` for CORS support
   - Controllers: `LocationController`, `BookingController`, `UsersController`, `HostController`
   - Authentication uses `X-User-Id` header for development (should be JWT/session in production)

2. **Service Layer** (`com.dani.luggagebackend.Service`)
   - Business logic
   - Transaction management
   - Services: `LocationService`, `BookingsService`, `UsersService`, `HostService`

3. **Repository Layer** (`com.dani.luggagebackend.Repo`)
   - Data access using Spring Data JPA
   - All repositories extend `JpaRepository<Entity, UUID>`
   - Custom queries for geospatial and relationship-based lookups

4. **Model Layer** (`com.dani.luggagebackend.Model`)
   - JPA entities
   - All entities use Lombok annotations (`@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Builder`)
   - All entities use UUID as primary keys

5. **DTO Layer** (`com.dani.luggagebackend.DTO`)
   - Request/Response data transfer objects
   - Validation annotations for input validation
   - Nested classes for related information (e.g., `HostInfo`, `UserInfo`, `LocationInfo`)

### Domain Model

**Core Entities:**

1. **Users** (`users` table)
   - UUID-based primary key
   - Email (unique), password hash, full name
   - Role enum: `USER`, `HOST`, `ADMIN` (default: `USER`)
   - Automatic timestamp management via `@PrePersist` and `@PreUpdate`

2. **Location** (`locations` table)
   - UUID-based primary key
   - **Host relationship**: Many-to-one with Users (LAZY fetch, required)
   - Name, address, coordinates (lat/lng)
   - Pricing (`pricePerHour` as BigDecimal)
   - Capacity and operating hours
   - Each location is owned and managed by a specific host user

3. **Booking** (`bookings` table)
   - UUID-based primary key
   - Many-to-one relationships with Users and Location (LAZY fetch)
   - Time period: `startTime`, `endTime` (Instant)
   - Price in cents (`priceCents` as Long)
   - Status enum: `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED` (default: `PENDING`)

### Key Relationships

- **Location → Users (Host)**: Many-to-one via `host_id` foreign key (required)
- **Booking → Users (Customer)**: Many-to-one via `user_id` foreign key
- **Booking → Location**: Many-to-one via `location_id` foreign key

All relationships use LAZY fetching to optimize performance. The host-location relationship enables hosts to manage their own locations and view associated bookings.

## Database Configuration

Database connection is configured in `src/main/resources/application.properties`:
- Uses PostgreSQL JDBC driver
- JPA is set to `ddl-auto=update` (schema auto-updates on entity changes)
- Connects to localhost:5432 by default (matches docker-compose setup)

## API Endpoints

### Customer/Public Endpoints (`/api/locations`)
- `POST /api/locations/nearby` - Find locations near user's position (with Haversine formula)
- `GET /api/locations` - Get all locations
- `GET /api/locations/{id}` - Get specific location

### Host Endpoints (require `X-User-Id` header)
- `POST /api/locations` - Create new location (HOST role only)
- `PUT /api/locations/{id}` - Update location (owner only)
- `DELETE /api/locations/{id}` - Delete location (owner only)
- `GET /api/locations/host/{hostId}` - Get all locations by host
- `GET /api/host/bookings` - View all bookings across host's locations
- `GET /api/host/locations/{locationId}/bookings` - View bookings for specific location
- `GET /api/host/dashboard` - Get booking statistics (total, pending, confirmed, cancelled, completed)

## Important Patterns

1. **Lombok Usage**: All model classes use Lombok to reduce boilerplate. Use `@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor` consistently.

2. **UUID Primary Keys**: All entities use UUID for primary keys with `@GeneratedValue`.

3. **Timestamp Management**: Entities that need timestamps should use `@PrePersist` and `@PreUpdate` lifecycle callbacks with `Instant` type.

4. **Enum Status Fields**: Use `@Enumerated(EnumType.STRING)` with `@Builder.Default` for status fields to ensure database readability and default values.

5. **Price Storage**: Prices are stored as:
   - `BigDecimal` for hourly rates (Location)
   - `Long` (cents) for total booking prices (Booking)

6. **Ownership Verification**: When hosts modify locations or view bookings, services verify ownership by checking `location.getHost().getId().equals(hostId)`.

7. **DTO Conversion**: Services use private `convertToResponse()` methods to convert entities to DTOs. DTOs include nested information objects (e.g., `HostInfo`, `UserInfo`) to avoid exposing full entity graphs.

8. **Geospatial Queries**: LocationRepo uses native SQL with Haversine formula for radius-based location searches. Results are ordered by distance.

9. **Authentication Pattern**: Controllers currently use `X-User-Id` header for user identification. This is for development/testing - production should use JWT tokens or session-based auth.