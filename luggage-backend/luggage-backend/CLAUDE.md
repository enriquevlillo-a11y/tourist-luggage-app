# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot backend application for a tourist luggage storage service. The platform connects three types of users:
1. **Customers** (USER role) - Book luggage storage at various locations
2. **Hosts** (HOST role) - Provide storage locations and manage bookings
3. **Admins** (ADMIN role) - System administration

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

**Important**: This application requires a LOCAL PostgreSQL instance to be stopped to avoid port conflicts.

Stop any local PostgreSQL services:
```bash
brew services stop postgresql@14
brew services stop postgresql@15
# Or check which services are running: brew services list
```

Start the Docker PostgreSQL database:
```bash
docker-compose up -d
```

Database credentials (defined in docker-compose.yml):
- Database: `luggage-backend`
- User: `luggo`
- Password: `luggo`
- Port: `5432`

Check database connection:
```bash
PGPASSWORD=luggo psql -h localhost -U luggo -d luggage-backend -c "SELECT version();"
```

### Mock Data

Mock data is loaded from `src/main/resources/data.sql` on first run. To reload mock data:

1. Clear existing data:
```bash
PGPASSWORD=luggo psql -h localhost -U luggo -d luggage-backend -c "TRUNCATE TABLE bookings, locations, users CASCADE;"
```

2. Set `spring.sql.init.mode=always` in `application.properties`

3. Restart the application

4. Set `spring.sql.init.mode=never` to prevent duplicate data on subsequent restarts

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

- **Spring Boot DevTools**: Enabled for automatic restarts during development
- **CORS**: All controllers use `@CrossOrigin` for cross-origin requests

## Architecture

### Layered Architecture

The application follows a standard Spring Boot layered architecture:

1. **Controller Layer** (`com.dani.luggagebackend.Controller`)
   - REST endpoints with `@RestController` and `@RequestMapping`
   - Request/response handling with DTOs
   - All controllers use `@CrossOrigin` for CORS support
   - Controllers: `LocationController`, `BookingController`, `UsersController`, `HostController`
   - **Authentication**: Uses `X-User-Id` header for development (should be JWT/session in production)

2. **Service Layer** (`com.dani.luggagebackend.Service`)
   - Business logic with `@Service` annotation
   - Transaction management with `@Transactional`
   - DTO conversion (entities → DTOs via private `convertToResponse()` methods)
   - Services: `LocationService`, `BookingsService`, `UsersService`, `HostService`

3. **Repository Layer** (`com.dani.luggagebackend.Repo`)
   - Data access using Spring Data JPA
   - All repositories extend `JpaRepository<Entity, UUID>`
   - Custom query methods using method name conventions
   - Native SQL queries for complex operations (e.g., geospatial searches)
   - Repositories: `LocationRepo`, `BookingRepo`, `UsersRepo`

4. **Model Layer** (`com.dani.luggagebackend.Model`)
   - JPA entities with `@Entity` and `@Table` annotations
   - All entities use Lombok: `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Builder`
   - All entities use UUID as primary keys with `@GeneratedValue`
   - Entities: `Users`, `Location`, `Booking`

5. **DTO Layer** (`com.dani.luggagebackend.DTO`)
   - Request DTOs: Validation with Jakarta annotations (`@NotBlank`, `@Email`, `@Positive`, etc.)
   - Response DTOs: Include nested DTOs to avoid exposing full entity graphs
   - Pattern: Use nested static classes for related info (e.g., `LocationResponse.HostInfo`, `BookingResponse.UserInfo`)

### Domain Model

**Core Entities:**

1. **Users** (`users` table) - `com.dani.luggagebackend.Model.Users`
   - UUID primary key
   - Fields: `email` (unique), `passwordHash`, `fullName`, `role`, `createdAt`, `updatedAt`
   - Role enum: `USER`, `HOST`, `ADMIN` (default: `USER`)
   - Timestamps managed via `@PrePersist` and `@PreUpdate` lifecycle callbacks
   - **Note**: In production, use BCrypt for password hashing (currently stores plain text for development)

2. **Location** (`locations` table) - `com.dani.luggagebackend.Model.Location`
   - UUID primary key
   - **Host relationship**: `@ManyToOne(fetch = FetchType.LAZY)` with Users via `host_id` foreign key (required)
   - Fields: `name`, `address`, `city`, `lat`, `lng`, `pricePerHour` (BigDecimal), `capacity`, `hours`, `isActive`
   - Each location is owned and managed by a specific host user
   - Active/inactive status for temporary closure without deletion

3. **Booking** (`bookings` table) - `com.dani.luggagebackend.Model.Booking`
   - UUID primary key
   - **Relationships**: `@ManyToOne(fetch = FetchType.LAZY)` with Users (customer) and Location
   - Fields: `startTime`, `endTime` (Instant), `priceCents` (Long), `status`
   - Status enum: `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED` (default: `PENDING`)
   - Price stored in cents for precision

### Key Relationships

- **Location → Users (Host)**: Many-to-one via `host_id` foreign key (required). Each location has exactly one host.
- **Booking → Users (Customer)**: Many-to-one via `user_id` foreign key. Each booking belongs to one customer.
- **Booking → Location**: Many-to-one via `location_id` foreign key. Each booking is for one location.

**Important**: All relationships use `FetchType.LAZY` to optimize performance and avoid N+1 query problems. Always use DTOs to control what data is returned to clients.

## Database Configuration

Database connection is configured in `src/main/resources/application.properties`:
- Connection: `jdbc:postgresql://localhost:5432/luggage-backend`
- JPA: `ddl-auto=update` (schema auto-updates on entity changes - use migrations in production)
- SQL Initialization: `spring.sql.init.mode=never` (set to `always` only for first-time data load)

## API Endpoints

### Users Module (`/api/users`) - COMPLETED ✅

**Authentication:**
- `POST /api/users/register` - Register new user account
- `POST /api/users/login` - User login (returns user info, would return JWT in production)

**Profile Management:**
- `GET /api/users/me` - Get current user profile (requires `X-User-Id` header)
- `GET /api/users/{userId}` - Get user by ID
- `PUT /api/users/{userId}` - Update user profile (requires `X-User-Id` header)
- `PUT /api/users/{userId}/password` - Change password (requires `X-User-Id` header)
- `DELETE /api/users/{userId}` - Delete user account (requires `X-User-Id` header)

**User Discovery:**
- `GET /api/users` - Get all users (admin endpoint)
- `GET /api/users/role/{role}` - Filter users by role (USER, HOST, ADMIN)
- `GET /api/users/search?q=query` - Search users by name or email
- `GET /api/users/by-email?email=` - Find user by email
- `GET /api/users/check-email?email=` - Check if email exists (useful for frontend validation)

**Role Management:**
- `PATCH /api/users/{userId}/upgrade-to-host` - Upgrade USER to HOST role

### Locations Module (`/api/locations`) - COMPLETED ✅

**Public/Customer Endpoints:**
- `POST /api/locations/nearby` - Find locations near user's coordinates (uses Haversine formula)
- `GET /api/locations` - Get all active locations
- `GET /api/locations/{id}` - Get specific location by ID
- `GET /api/locations/search?q=query` - Search locations by name/address
- `GET /api/locations/cities` - Get all unique cities with locations
- `GET /api/locations/city/{city}` - Get locations by city
- `GET /api/locations/popular?limit=10` - Get popular locations by booking count
- `GET /api/locations/{id}/availability?startTime=&endTime=&capacity=` - Check availability

**Filtering:**
- `GET /api/locations/filter?minPrice=&maxPrice=&minCapacity=&city=` - Filter locations
- `POST /api/locations/nearby/filtered` - Find nearby locations with filters

**Host Management (require `X-User-Id` header):**
- `POST /api/locations` - Create new location (HOST role required)
- `PUT /api/locations/{id}` - Update location (owner only)
- `DELETE /api/locations/{id}` - Delete location (owner only)
- `GET /api/locations/host/{hostId}` - Get all locations by host
- `PATCH /api/locations/{id}/status` - Toggle active/inactive status

### Host Module (`/api/host`) - COMPLETED ✅

**Booking Management (require `X-User-Id` header):**
- `GET /api/host/bookings` - View all bookings across all host's locations
- `GET /api/host/locations/{locationId}/bookings` - View bookings for specific location

**Dashboard:**
- `GET /api/host/dashboard` - Get booking statistics (total, pending, confirmed, cancelled, completed)

### Bookings Module (`/api/bookings`) - NOT YET IMPLEMENTED ⚠️

**Expected endpoints (to be implemented):**
- `POST /api/bookings` - Create new booking
- `GET /api/bookings/{id}` - Get booking by ID
- `GET /api/bookings/user/{userId}` - Get user's bookings
- `PUT /api/bookings/{id}` - Update booking
- `PATCH /api/bookings/{id}/cancel` - Cancel booking
- `PATCH /api/bookings/{id}/confirm` - Confirm booking (host only)
- `PATCH /api/bookings/{id}/complete` - Mark as completed

## Important Patterns & Conventions

### 1. Lombok Usage
All model classes use Lombok to reduce boilerplate:
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "table_name")
public class EntityName {
    // fields
}
```

### 2. UUID Primary Keys
All entities use UUID for primary keys:
```java
@Id
@GeneratedValue
private UUID id;
```

### 3. Timestamp Management
Use `@PrePersist` and `@PreUpdate` lifecycle callbacks with `Instant` type:
```java
private Instant createdAt;
private Instant updatedAt;

@PrePersist
protected void onCreate() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
}

@PreUpdate
protected void onUpdate() {
    updatedAt = Instant.now();
}
```

### 4. Enum Status Fields
Use `@Enumerated(EnumType.STRING)` with `@Builder.Default`:
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
@Builder.Default
private Status status = Status.PENDING;
```

### 5. Price Storage
- **Hourly rates**: Use `BigDecimal` (Location.pricePerHour)
- **Total prices**: Use `Long` in cents (Booking.priceCents)

### 6. Ownership Verification
Always verify ownership in services before allowing modifications:
```java
if (!location.getHost().getId().equals(hostId)) {
    throw new RuntimeException("Unauthorized");
}
```

### 7. DTO Conversion Pattern
Services use private `convertToResponse()` methods:
```java
private EntityResponse convertToResponse(Entity entity) {
    return EntityResponse.builder()
        .id(entity.getId())
        // Include nested DTOs, not full entities
        .relatedInfo(buildRelatedInfo(entity.getRelation()))
        .build();
}
```

### 8. Nested DTOs
Use static nested classes to group related information:
```java
@Data
@Builder
public static class HostInfo {
    private UUID id;
    private String fullName;
    private String email;
    // Don't include passwordHash or other sensitive fields
}
```

### 9. Geospatial Queries
Use native SQL with Haversine formula for distance calculations:
```sql
SELECT *,
  (6371 * acos(cos(radians(?1)) * cos(radians(lat)) *
   cos(radians(lng) - radians(?2)) + sin(radians(?1)) *
   sin(radians(lat)))) AS distance
FROM locations
WHERE /* conditions */
ORDER BY distance
```

### 10. Authentication Pattern (Development)
Controllers use `X-User-Id` header for user identification:
```java
@GetMapping("/me")
public ResponseEntity<UserResponse> getCurrentUser(
    @RequestHeader("X-User-Id") UUID userId) {
    // ...
}
```

**Important**: This is for development/testing only. Production should use JWT tokens or session-based auth.

### 11. Repository Query Methods
Use Spring Data JPA method naming conventions:
```java
Optional<Users> findByEmail(String email);
boolean existsByEmail(String email);
List<Location> findByHostId(UUID hostId);
List<Users> findByFullNameContainingIgnoreCase(String name);
```

### 12. Validation
Use Jakarta validation annotations on DTOs:
```java
@NotBlank(message = "Email is required")
@Email(message = "Email must be valid")
private String email;

@Positive(message = "Price must be positive")
private BigDecimal pricePerHour;
```

## Mock Data

The application includes comprehensive mock data in `src/main/resources/data.sql`:

**Users (9 total):**
- 4 Customers (USER role): John Doe, Sarah Wilson, Mike Chen, Emma Brown
- 4 Hosts (HOST role): Maria Garcia, David Kim, Lisa Anderson, James Murphy
- 1 Admin (ADMIN role): System Admin

**Locations (8 total):**
- 2 locations per host in different cities (New York, Paris, Tokyo, London)
- Each location has name, address, coordinates, pricing, capacity, and hours

**Bookings (8 total):**
- Mix of statuses: CONFIRMED (3), PENDING (2), COMPLETED (2), CANCELLED (1)
- Associated with different customers and locations

**Accessing Mock Data:**
All users have password `password123` (Admin has `admin123`)

## Common Development Tasks

### Adding a New Entity

1. Create entity class in `Model/` with Lombok annotations
2. Create repository interface in `Repo/` extending `JpaRepository<Entity, UUID>`
3. Create DTOs in `DTO/` for requests and responses
4. Create service class in `Service/` with business logic
5. Create controller in `Controller/` with REST endpoints
6. Update mock data in `data.sql` if needed

### Adding a New Endpoint

1. Add method to appropriate service class
2. Add controller method with proper annotations:
   - `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`
   - `@RequestBody`, `@PathVariable`, `@RequestParam`, `@RequestHeader` as needed
   - `@Valid` for validation
3. Return `ResponseEntity<T>` with appropriate HTTP status codes

### Testing Endpoints with curl

Example login:
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email": "john.doe@email.com", "password": "password123"}'
```

Example with authentication header:
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "X-User-Id: 11111111-1111-1111-1111-111111111111"
```

## Known Issues & Production Notes

1. **Password Storage**: Currently stores plain text passwords. Implement BCrypt hashing for production.
2. **Authentication**: Uses simple `X-User-Id` header. Implement JWT tokens or session-based auth for production.
3. **SQL Initialization**: Set `spring.sql.init.mode=never` after first run to avoid duplicate data errors.
4. **Database Port Conflict**: Local PostgreSQL services must be stopped before starting Docker PostgreSQL.
5. **Error Handling**: Uses generic `RuntimeException`. Implement custom exceptions and global exception handler for production.
6. **Soft Delete**: Currently uses hard delete. Consider implementing soft delete for users and locations.
7. **Cascade Operations**: Be careful with entity deletions - may need to handle bookings when deleting users/locations.