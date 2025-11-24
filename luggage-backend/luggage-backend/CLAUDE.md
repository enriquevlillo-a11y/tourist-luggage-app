# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot backend application for a tourist luggage storage service. The platform connects three types of users:
1. **Customers** (USER role) - Book luggage storage at various locations
2. **Hosts** (HOST role) - Provide storage locations and manage bookings
3. **Admins** (ADMIN role) - System administration

**Tech Stack:**
- Spring Boot 3.5.6
- Java 21
- PostgreSQL 14
- Spring Data JPA
- Spring Security (JWT authentication)
- Flyway (Database migrations - currently disabled in development)
- Project Lombok
- Maven
- Jakarta Validation
- JJWT 0.12.3 (JWT library)
- BCrypt (password hashing)
- Bucket4j 8.7.0 (Rate limiting)

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

The application runs on `http://localhost:8081`

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

## Additional Documentation

This repository includes comprehensive companion documentation:
- **DATABASE.md** - Detailed database schema, queries, and operations
- **SECURITY.md** - Security best practices, environment variables, and production checklist

Refer to these files for in-depth information on specific topics.

## Architecture

### Layered Architecture

The application follows a standard Spring Boot layered architecture:

1. **Controller Layer** (`com.dani.luggagebackend.Controller`)
   - REST endpoints with `@RestController` and `@RequestMapping`
   - Request/response handling with DTOs
   - All controllers use `@CrossOrigin` for CORS support
   - Controllers: `LocationController`, `BookingController`, `UsersController`, `HostController`
   - **Authentication**: Uses JWT tokens via `Authorization: Bearer <token>` header
   - Extracts userId from `SecurityContextHolder.getContext().getAuthentication().getPrincipal()`

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

6. **Security Layer** (`com.dani.luggagebackend.Security`)
   - **JwtUtil**: JWT token generation, parsing, and validation (extracts claims, verifies signatures)
   - **JwtAuthenticationFilter**: Servlet filter that intercepts requests, extracts JWT from Authorization header, validates token, and sets authentication in SecurityContext
   - **SecurityConfig**: Spring Security configuration defining public endpoints, CORS settings, and authentication requirements

7. **Exception Layer** (`com.dani.luggagebackend.Exception`)
   - **GlobalExceptionHandler**: `@RestControllerAdvice` that catches exceptions and returns standardized JSON error responses
   - Custom exceptions: `ResourceNotFoundException`, `BadRequestException`, `UnauthorizedException`, `ForbiddenException`, `RateLimitExceededException`
   - **ErrorResponse**: DTO for consistent error responses with timestamp, status, error type, message, path, and optional validation errors

8. **Rate Limiting** (`com.dani.luggagebackend.Service.RateLimitService`)
   - Uses Bucket4j token bucket algorithm to prevent brute-force attacks
   - **Auth endpoints** (login/register): 5 requests per minute per IP
   - **General API endpoints**: 100 requests per minute per IP
   - In-memory cache using `ConcurrentHashMap` (consider Redis for production distributed systems)

### Domain Model

**Core Entities:**

1. **Users** (`users` table) - `com.dani.luggagebackend.Model.Users`
   - UUID primary key
   - Fields: `email` (unique), `passwordHash`, `fullName`, `role`, `createdAt`, `updatedAt`
   - Role enum: `USER`, `HOST`, `ADMIN` (default: `USER`)
   - Timestamps managed via `@PrePersist` and `@PreUpdate` lifecycle callbacks
   - **Password Security**: Uses BCrypt hashing for secure password storage

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
- JWT Configuration: `jwt.secret` and `jwt.expiration` (24 hours = 86400000ms)

## JWT Authentication & Security

The application uses JWT (JSON Web Tokens) for stateless authentication with Spring Security.

### Security Architecture

**Components:**
1. **JwtUtil** (`com.dani.luggagebackend.Security.JwtUtil`) - Token generation and validation
2. **JwtAuthenticationFilter** (`com.dani.luggagebackend.Security.JwtAuthenticationFilter`) - Request interceptor
3. **SecurityConfig** (`com.dani.luggagebackend.Security.SecurityConfig`) - Spring Security configuration

### Authentication Flow

1. **Registration/Login**:
   - User registers or logs in with email/password
   - Password is hashed with BCrypt before storage/comparison
   - Server generates JWT token with user claims (userId, email, role)
   - Token returned in `LoginResponse.token` field

2. **Authenticated Requests**:
   - Client includes JWT in `Authorization: Bearer <token>` header
   - `JwtAuthenticationFilter` intercepts request and validates token
   - User information extracted and stored in `SecurityContext`
   - Controllers extract userId via `SecurityContextHolder.getContext().getAuthentication().getPrincipal()`

3. **Public Endpoints** (no authentication required):
   - User registration/login
   - Location browsing (nearby, search, filter, cities, popular, etc.)
   - Email availability check

### JWT Token Structure

Tokens include the following claims:
```json
{
  "userId": "uuid-string",
  "email": "user@example.com",
  "role": "USER|HOST|ADMIN",
  "sub": "user@example.com",
  "iat": 1234567890,
  "exp": 1234654290
}
```

### Password Security

- **BCrypt Hashing**: All passwords are hashed using BCrypt (via `PasswordEncoder`)
- **Registration**: `UsersService.register()` hashes password before saving
- **Login**: `UsersService.login()` uses `passwordEncoder.matches()` to verify
- **Password Change**: `UsersService.changePassword()` verifies current password and hashes new one

### Usage Pattern in Controllers

```java
// Extract authenticated user ID from SecurityContext
UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

// Use the userId for authorization checks
if (!resource.getOwnerId().equals(userId)) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
}
```

### Configuration

JWT settings in `application.properties`:
```properties
jwt.secret=<base64-encoded-secret>
jwt.expiration=86400000  # 24 hours in milliseconds
```

**Important**: In production, store `jwt.secret` as an environment variable, not in the properties file.

## API Endpoints

### Users Module (`/api/users`) - COMPLETED ✅

**Authentication (Public):**
- `POST /api/users/register` - Register new user account (returns JWT token)
- `POST /api/users/login` - User login (returns JWT token)
- `GET /api/users/check-email?email=` - Check if email exists

**Profile Management (Authenticated):**
- `GET /api/users/me` - Get current user profile
- `PUT /api/users/{userId}` - Update user profile (own profile only)
- `PUT /api/users/{userId}/password` - Change password (own account only)
- `DELETE /api/users/{userId}` - Delete user account (own account only)
- `PATCH /api/users/{userId}/upgrade-to-host` - Upgrade USER to HOST role (own account only)

**User Discovery (Authenticated):**
- `GET /api/users/{userId}` - Get user by ID
- `GET /api/users` - Get all users (admin endpoint)
- `GET /api/users/role/{role}` - Filter users by role (USER, HOST, ADMIN)
- `GET /api/users/search?q=query` - Search users by name or email
- `GET /api/users/by-email?email=` - Find user by email

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

**Host Management (Authenticated, HOST role):**
- `POST /api/locations` - Create new location
- `PUT /api/locations/{id}` - Update location (owner only)
- `DELETE /api/locations/{id}` - Delete location (owner only)
- `GET /api/locations/host/{hostId}` - Get all locations by host
- `PATCH /api/locations/{id}/status` - Toggle active/inactive status

### Host Module (`/api/host`) - COMPLETED ✅

**Booking Management (Authenticated, HOST role):**
- `GET /api/host/bookings` - View all bookings across all host's locations
- `GET /api/host/locations/{locationId}/bookings` - View bookings for specific location
- `GET /api/host/dashboard` - Get booking statistics

### Bookings Module (`/api/bookings`) - COMPLETED ✅

**Customer Endpoints (Authenticated):**
- `POST /api/bookings` - Create new booking
- `GET /api/bookings` - Get all bookings (admin)
- `GET /api/bookings/{id}` - Get booking by ID
- `GET /api/bookings/me` - Get current user's bookings
- `GET /api/bookings/user/{userId}` - Get user's bookings
- `PUT /api/bookings/{id}` - Update booking (owner only)
- `DELETE /api/bookings/{id}` - Cancel booking (owner only)

**Host Endpoints (Authenticated, HOST role):**
- `PATCH /api/bookings/{bookingId}/confirm` - Confirm booking (location owner only)
- `PATCH /api/bookings/{bookingId}/complete` - Mark booking as completed (location owner only)

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

### 10. JWT Authentication Pattern
Controllers extract authenticated user ID from SecurityContext:
```java
@GetMapping("/me")
public ResponseEntity<UserResponse> getCurrentUser() {
    UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // Use userId for authorization checks
    return usersService.getUserById(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

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

### 13. Exception Handling Pattern
Use custom exceptions instead of generic RuntimeException for better error handling:
```java
// BAD - Don't use generic RuntimeException
if (!location.isPresent()) {
    throw new RuntimeException("Location not found");
}

// GOOD - Use custom exceptions
if (!location.isPresent()) {
    throw new ResourceNotFoundException("Location not found with id: " + locationId);
}

if (!user.getId().equals(authenticatedUserId)) {
    throw new ForbiddenException("You don't have permission to modify this resource");
}
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

**Step 1: Login to get JWT token**
```bash
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email": "john.doe@email.com", "password": "password123"}'
```

Response will include a JWT token:
```json
{
  "userId": "11111111-1111-1111-1111-111111111111",
  "email": "john.doe@email.com",
  "fullName": "John Doe",
  "role": "USER",
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Step 2: Use JWT token for authenticated requests**
```bash
# Save the token
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Make authenticated request
curl -X GET http://localhost:8081/api/users/me \
  -H "Authorization: Bearer $TOKEN"
```

**Example: Create a booking**
```bash
curl -X POST http://localhost:8081/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "locationId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
    "startTime": "2025-02-01T10:00:00Z",
    "endTime": "2025-02-01T16:00:00Z",
    "numberOfItems": 2
  }'
```

## Error Handling & Exception Management

The application implements comprehensive error handling using Spring's `@RestControllerAdvice`:

### Custom Exceptions
- **ResourceNotFoundException** (404): Entity not found
- **BadRequestException** (400): Invalid request data
- **UnauthorizedException** (401): Authentication required/failed
- **ForbiddenException** (403): Insufficient permissions
- **RateLimitExceededException** (429): Too many requests

### ErrorResponse Structure
All error responses follow a consistent format:
```json
{
  "timestamp": "2025-01-24T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Descriptive error message",
  "path": "/api/users/login",
  "validationErrors": {
    "email": "Email must be valid",
    "password": "Password is required"
  }
}
```

### Usage Pattern
```java
// In services, throw custom exceptions instead of generic RuntimeException
if (!entity.isPresent()) {
    throw new ResourceNotFoundException("User not found with id: " + userId);
}

if (!user.getRole().equals(Role.HOST)) {
    throw new ForbiddenException("Only hosts can create locations");
}
```

## Rate Limiting

The application uses **Bucket4j** to implement token bucket rate limiting:

### Configuration
- **Authentication endpoints** (`/api/users/login`, `/api/users/register`): 5 requests/minute per IP
- **General API endpoints**: 100 requests/minute per IP
- Uses in-memory `ConcurrentHashMap` for bucket storage

### Implementation Pattern
Controllers or filters can use `RateLimitService` to enforce limits:
```java
Bucket bucket = rateLimitService.resolveAuthBucket(clientIp);
if (!bucket.tryConsume(1)) {
    throw new RateLimitExceededException("Rate limit exceeded. Please try again later.");
}
```

**Production Note**: For distributed systems, replace in-memory cache with Redis or similar distributed cache.

## Database Migrations

The application includes **Flyway** for database schema version control and migrations.

### Current Status
- **Development**: Flyway is **disabled** (`spring.flyway.enabled=false`) to avoid circular dependency with JPA
- Schema managed by JPA with `spring.jpa.hibernate.ddl-auto=update`
- Migration files exist in `src/main/resources/db/migration/` but are not executed

### Migration Files

Located in `src/main/resources/db/migration/`:
- `V1__Init.sql` - Initial schema creation (users, locations, bookings tables)

### Enabling Flyway (Production)

To enable Flyway for production:

1. Set `spring.flyway.enabled=true` in `application-prod.properties`
2. Set `spring.jpa.hibernate.ddl-auto=validate` (never use `update`)
3. Ensure migration files are up-to-date with current schema
4. Flyway will automatically run migrations on startup and track them in `flyway_schema_history` table

### Creating New Migrations

1. Create a new file: `src/main/resources/db/migration/V{number}__{description}.sql`
   - Example: `V2__Add_user_phone_number.sql`
2. Naming convention: `V{version}__{description}.sql` (double underscore!)
3. Test migrations in development before deploying to production

### Known Issue: Flyway Circular Dependency

If you encounter "Circular depends-on relationship between 'flyway' and 'entityManagerFactory'":
- **Solution**: Set `spring.flyway.enabled=false` and use `spring.jpa.hibernate.ddl-auto=update` for development
- **For Production**: Enable Flyway and set `ddl-auto=validate` to use proper migrations

## AWS Deployment

The application is configured for deployment to AWS Elastic Beanstalk.

### Deployment Files

- **Procfile** - Defines how to run the JAR on AWS
- **.ebextensions/app.config** - Elastic Beanstalk environment configuration
- **application-prod.properties** - Production environment settings

### Building for Production

```bash
# Build JAR (skipping tests for faster builds)
./mvnw clean package -DskipTests

# JAR location: target/luggage-backend-0.0.1-SNAPSHOT.jar
```

### AWS Elastic Beanstalk Deployment

**Prerequisites:**
```bash
# Install AWS CLI
brew install awscli

# Configure AWS credentials
aws configure

# Install Elastic Beanstalk CLI
brew install aws-elasticbeanstalk
```

**Initialize and Deploy:**
```bash
# Initialize EB application (one-time setup)
eb init
# Select: Region, Application name, Platform (Corretto 21)

# Create environment (one-time setup)
eb create luggage-backend-prod --single
# Use --single flag to avoid load balancer costs during development

# Set environment variables
eb setenv DATABASE_URL="jdbc:postgresql://your-rds-endpoint:5432/luggagedb"
eb setenv DB_USERNAME="your-db-username"
eb setenv DB_PASSWORD="your-db-password"
eb setenv JWT_SECRET="$(openssl rand -base64 64 | tr -d '\n')"
eb setenv SPRING_PROFILES_ACTIVE="prod"

# Deploy application
./mvnw clean package -DskipTests
eb deploy

# Open application in browser
eb open
```

### RDS Database Setup

1. Create RDS PostgreSQL instance via AWS Console
2. Configure security group to allow connections from Elastic Beanstalk
3. Use RDS endpoint in `DATABASE_URL` environment variable
4. Flyway will automatically run migrations on first deployment

### Environment Configuration

Production environment uses `application-prod.properties`:
- Server runs on port 5000 (required by Elastic Beanstalk)
- Database connection via environment variables
- JWT secret from environment variable
- SQL initialization disabled (uses Flyway instead)
- Production logging levels (INFO)

## Production Notes

1. **JWT Secret**: Store `jwt.secret` as an environment variable, not in application.properties
2. **Database Migrations**: Enable Flyway (`spring.flyway.enabled=true`) and set `ddl-auto=validate` (never use `update`)
3. **Database Port Conflict**: Local PostgreSQL services must be stopped before starting Docker PostgreSQL
4. **Error Handling**: ✅ Implemented - Uses custom exceptions and GlobalExceptionHandler for standardized error responses
5. **Rate Limiting**: ✅ Implemented - Uses Bucket4j in-memory cache. For distributed systems, migrate to Redis
6. **Soft Delete**: Currently uses hard delete. Consider implementing soft delete for users and locations
7. **Cascade Operations**: Be careful with entity deletions - may need to handle bookings when deleting users/locations
8. **CORS Configuration**: Currently allows all origins (`*`). In production, restrict to specific frontend domains
9. **Token Expiration**: Currently set to 24 hours. Adjust based on security requirements
10. **JJWT Deprecations**: Some JJWT methods are deprecated (SignatureAlgorithm). Consider updating to newer API patterns
11. **SSL/TLS**: Production deployment should use HTTPS. AWS Elastic Beanstalk can handle SSL certificates via AWS Certificate Manager