# DATABASE.md

Database documentation for the Luggage Storage Application.

## Overview

**Database Management System:** PostgreSQL 14
**Schema Management:** JPA/Hibernate (DDL auto-update)
**Connection:** `jdbc:postgresql://localhost:5432/luggage-backend`
**Database Name:** `luggage-backend`
**User:** `luggo`
**Password:** `luggo` (development only)

The application uses a relational database with three main tables: `users`, `locations`, and `bookings`. All tables use UUID as primary keys for better distribution and security.

## Database Schema

### Entity Relationship Diagram

```
┌─────────────────┐
│     users       │
│─────────────────│
│ id (UUID) PK    │
│ email           │◄───────────┐
│ password_hash   │            │
│ full_name       │            │
│ role            │            │
│ created_at      │            │
│ updated_at      │            │
└─────────────────┘            │
         △                     │
         │                     │
         │                     │
         │ host_id (FK)        │ user_id (FK)
         │                     │
┌─────────────────┐       ┌────────────────┐
│   locations     │       │   bookings     │
│─────────────────│       │────────────────│
│ id (UUID) PK    │       │ id (UUID) PK   │
│ name            │◄──────│ location_id FK │
│ address         │       │ user_id FK     │────┘
│ city            │       │ start_time     │
│ lat             │       │ end_time       │
│ lng             │       │ price_cents    │
│ price_per_hour  │       │ status         │
│ capacity        │       │ created_at     │
│ hours           │       │ updated_at     │
│ is_active       │       └────────────────┘
│ host_id FK      │
│ created_at      │
│ updated_at      │
└─────────────────┘
```

## Tables

### 1. users

Stores all user accounts including customers, hosts, and administrators.

**Table Name:** `users`

| Column         | Type                    | Constraints                          | Description                          |
|----------------|-------------------------|--------------------------------------|--------------------------------------|
| id             | UUID                    | PRIMARY KEY, NOT NULL                | Unique user identifier               |
| email          | VARCHAR(255)            | UNIQUE, NOT NULL                     | User email address                   |
| password_hash  | VARCHAR(255)            | NOT NULL                             | BCrypt hashed password               |
| full_name      | VARCHAR(255)            | NOT NULL                             | User's full name                     |
| role           | VARCHAR(50)             | NOT NULL, DEFAULT 'USER'             | USER, HOST, or ADMIN                 |
| created_at     | TIMESTAMP               | NOT NULL                             | Account creation timestamp           |
| updated_at     | TIMESTAMP               | NOT NULL                             | Last update timestamp                |

**Indexes:**
- Primary key index on `id`
- Unique index on `email`
- Index on `role` (for role-based queries)

**Constraints:**
- `email` must be unique
- `role` enum: `USER`, `HOST`, `ADMIN`
- Timestamps are automatically managed via JPA lifecycle callbacks

**Sample Data:**
```sql
-- Customer
('11111111-1111-1111-1111-111111111111', 'john.doe@email.com', '$2a$10$...', 'John Doe', 'USER')

-- Host
('55555555-5555-5555-5555-555555555555', 'maria.garcia@hotel.com', '$2a$10$...', 'Maria Garcia', 'HOST')

-- Admin
('99999999-9999-9999-9999-999999999999', 'admin@luggage.com', '$2a$10$...', 'System Admin', 'ADMIN')
```

---

### 2. locations

Stores luggage storage locations managed by hosts.

**Table Name:** `locations`

| Column         | Type                    | Constraints                          | Description                          |
|----------------|-------------------------|--------------------------------------|--------------------------------------|
| id             | UUID                    | PRIMARY KEY, NOT NULL                | Unique location identifier           |
| name           | VARCHAR(255)            | NOT NULL                             | Location name                        |
| address        | VARCHAR(500)            | NOT NULL                             | Street address                       |
| city           | VARCHAR(100)            | NOT NULL                             | City name                            |
| lat            | DOUBLE PRECISION        | NOT NULL                             | Latitude coordinate                  |
| lng            | DOUBLE PRECISION        | NOT NULL                             | Longitude coordinate                 |
| price_per_hour | DECIMAL(10,2)           | NOT NULL                             | Hourly storage price                 |
| capacity       | INTEGER                 | NOT NULL                             | Maximum storage capacity             |
| hours          | VARCHAR(100)            | NOT NULL                             | Operating hours (text)               |
| is_active      | BOOLEAN                 | NOT NULL, DEFAULT true               | Location active status               |
| host_id        | UUID                    | FOREIGN KEY REFERENCES users(id)     | Host user who owns this location     |
| created_at     | TIMESTAMP               | NOT NULL                             | Location creation timestamp          |
| updated_at     | TIMESTAMP               | NOT NULL                             | Last update timestamp                |

**Indexes:**
- Primary key index on `id`
- Index on `host_id` (for host's location queries)
- Index on `city` (for city-based searches)
- Composite index on `(lat, lng)` (for geospatial queries)
- Index on `is_active` (for filtering active locations)

**Constraints:**
- `host_id` must reference a valid user with HOST role
- `capacity` must be positive
- `price_per_hour` must be positive
- `lat` range: -90 to 90
- `lng` range: -180 to 180

**Sample Data:**
```sql
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Times Square Luggage Hub', '1560 Broadway',
 'New York', 40.7580, -73.9855, 5.00, 50, '24/7', true,
 '55555555-5555-5555-5555-555555555555')
```

**Geospatial Queries:**

The application uses the Haversine formula for distance calculations:

```sql
SELECT *,
  (6371 * acos(cos(radians(?1)) * cos(radians(lat)) *
   cos(radians(lng) - radians(?2)) + sin(radians(?1)) *
   sin(radians(lat)))) AS distance
FROM locations
WHERE is_active = true
ORDER BY distance
LIMIT 20;
```

---

### 3. bookings

Stores luggage storage bookings made by customers.

**Table Name:** `bookings`

| Column         | Type                    | Constraints                          | Description                          |
|----------------|-------------------------|--------------------------------------|--------------------------------------|
| id             | UUID                    | PRIMARY KEY, NOT NULL                | Unique booking identifier            |
| user_id        | UUID                    | FOREIGN KEY REFERENCES users(id)     | Customer who made the booking        |
| location_id    | UUID                    | FOREIGN KEY REFERENCES locations(id) | Location for storage                 |
| start_time     | TIMESTAMP               | NOT NULL                             | Booking start time                   |
| end_time       | TIMESTAMP               | NOT NULL                             | Booking end time                     |
| price_cents    | BIGINT                  | NOT NULL                             | Total price in cents                 |
| status         | VARCHAR(50)             | NOT NULL, DEFAULT 'PENDING'          | Booking status                       |
| created_at     | TIMESTAMP               | NOT NULL                             | Booking creation timestamp           |
| updated_at     | TIMESTAMP               | NOT NULL                             | Last update timestamp                |

**Indexes:**
- Primary key index on `id`
- Index on `user_id` (for user's booking queries)
- Index on `location_id` (for location's booking queries)
- Index on `status` (for status-based filtering)
- Composite index on `(start_time, end_time)` (for time-based queries)

**Constraints:**
- `user_id` must reference a valid user
- `location_id` must reference a valid location
- `end_time` must be after `start_time`
- `price_cents` must be non-negative
- `status` enum: `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`

**Status Lifecycle:**
```
PENDING → CONFIRMED → COMPLETED
   ↓
CANCELLED
```

**Sample Data:**
```sql
-- Confirmed booking
('b1b1b1b1-b1b1-b1b1-b1b1-b1b1b1b1b1b1',
 '11111111-1111-1111-1111-111111111111',  -- John Doe
 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',  -- Times Square Hub
 '2025-01-28 10:00:00', '2025-01-30 10:00:00', 7200, 'CONFIRMED')
```

## Relationships

### users → locations (One-to-Many)
- **Type:** One user (HOST) can have many locations
- **Foreign Key:** `locations.host_id` → `users.id`
- **Fetch Type:** LAZY
- **Cascade:** None (manual handling required)
- **Description:** Each location must belong to exactly one host user

### users → bookings (One-to-Many)
- **Type:** One user can have many bookings
- **Foreign Key:** `bookings.user_id` → `users.id`
- **Fetch Type:** LAZY
- **Cascade:** None (manual handling required)
- **Description:** Each booking belongs to exactly one customer user

### locations → bookings (One-to-Many)
- **Type:** One location can have many bookings
- **Foreign Key:** `bookings.location_id` → `locations.id`
- **Fetch Type:** LAZY
- **Cascade:** None (manual handling required)
- **Description:** Each booking is associated with exactly one location

## Common Queries

### User Queries

**Find user by email:**
```sql
SELECT * FROM users WHERE email = ?;
```

**Check if email exists:**
```sql
SELECT EXISTS(SELECT 1 FROM users WHERE email = ?);
```

**Get users by role:**
```sql
SELECT * FROM users WHERE role = ?;
```

**Search users by name:**
```sql
SELECT * FROM users WHERE LOWER(full_name) LIKE LOWER(?);
```

### Location Queries

**Find locations near coordinates (within radius):**
```sql
SELECT *,
  (6371 * acos(cos(radians(?)) * cos(radians(lat)) *
   cos(radians(lng) - radians(?)) + sin(radians(?)) *
   sin(radians(lat)))) AS distance
FROM locations
WHERE is_active = true
  AND (6371 * acos(cos(radians(?)) * cos(radians(lat)) *
       cos(radians(lng) - radians(?)) + sin(radians(?)) *
       sin(radians(lat)))) <= ?
ORDER BY distance;
```

**Get all locations by host:**
```sql
SELECT * FROM locations WHERE host_id = ?;
```

**Get locations by city:**
```sql
SELECT * FROM locations WHERE city = ? AND is_active = true;
```

**Filter locations by price and capacity:**
```sql
SELECT * FROM locations
WHERE is_active = true
  AND price_per_hour BETWEEN ? AND ?
  AND capacity >= ?
ORDER BY price_per_hour;
```

**Get popular locations (by booking count):**
```sql
SELECT l.*, COUNT(b.id) as booking_count
FROM locations l
LEFT JOIN bookings b ON l.id = b.location_id
WHERE l.is_active = true
GROUP BY l.id
ORDER BY booking_count DESC
LIMIT ?;
```

**Check location availability:**
```sql
SELECT COUNT(*) as active_bookings
FROM bookings
WHERE location_id = ?
  AND status IN ('PENDING', 'CONFIRMED')
  AND NOT (end_time <= ? OR start_time >= ?);
```

### Booking Queries

**Get user's bookings:**
```sql
SELECT * FROM bookings WHERE user_id = ? ORDER BY start_time DESC;
```

**Get location's bookings:**
```sql
SELECT * FROM bookings WHERE location_id = ? ORDER BY start_time DESC;
```

**Get host's bookings (across all locations):**
```sql
SELECT b.*
FROM bookings b
INNER JOIN locations l ON b.location_id = l.id
WHERE l.host_id = ?
ORDER BY b.start_time DESC;
```

**Get bookings by status:**
```sql
SELECT * FROM bookings WHERE status = ? ORDER BY start_time;
```

**Get booking statistics for host:**
```sql
SELECT
  COUNT(*) as total_bookings,
  SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as pending,
  SUM(CASE WHEN status = 'CONFIRMED' THEN 1 ELSE 0 END) as confirmed,
  SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled,
  SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed
FROM bookings b
INNER JOIN locations l ON b.location_id = l.id
WHERE l.host_id = ?;
```

## Mock Data

The application includes comprehensive mock data in `src/main/resources/data.sql`:

### Users (9 total)

**Customers (USER role):**
- John Doe - `john.doe@email.com`
- Sarah Wilson - `sarah.wilson@email.com`
- Mike Chen - `mike.chen@email.com`
- Emma Brown - `emma.brown@email.com`

**Hosts (HOST role):**
- Maria Garcia - `maria.garcia@hotel.com` (2 locations in New York)
- David Kim - `david.kim@hostel.com` (2 locations in Paris)
- Lisa Anderson - `lisa.anderson@cafe.com` (2 locations in Tokyo)
- James Murphy - `james.murphy@shop.com` (2 locations in London)

**Admins (ADMIN role):**
- System Admin - `admin@luggage.com`

All user passwords are `password123` (except admin: `admin123`)
**Note:** Passwords are now BCrypt hashed in the actual database.

### Locations (8 total)

Distributed across 4 cities: New York (2), Paris (2), Tokyo (2), London (2)

### Bookings (8 total)

- **Confirmed:** 3 bookings
- **Pending:** 2 bookings
- **Completed:** 2 bookings
- **Cancelled:** 1 booking

## Database Operations

### Initial Setup

1. **Start PostgreSQL with Docker:**
```bash
docker-compose up -d
```

2. **Verify connection:**
```bash
PGPASSWORD=luggo psql -h localhost -U luggo -d luggage-backend -c "SELECT version();"
```

3. **Load mock data:**
   - Set `spring.sql.init.mode=always` in `application.properties`
   - Run the application (loads `data.sql`)
   - Set `spring.sql.init.mode=never` to prevent reloading

### Clearing Data

**Clear all data (cascade delete):**
```bash
PGPASSWORD=luggo psql -h localhost -U luggo -d luggage-backend -c \
  "TRUNCATE TABLE bookings, locations, users CASCADE;"
```

**Clear specific table:**
```bash
PGPASSWORD=luggo psql -h localhost -U luggo -d luggage-backend -c \
  "DELETE FROM bookings WHERE status = 'CANCELLED';"
```

### Backup and Restore

**Backup database:**
```bash
PGPASSWORD=luggo pg_dump -h localhost -U luggo -d luggage-backend > backup.sql
```

**Restore database:**
```bash
PGPASSWORD=luggo psql -h localhost -U luggo -d luggage-backend < backup.sql
```

## Performance Considerations

### Recommended Indexes

The following indexes are automatically created by JPA based on entity relationships:

1. **Primary Keys:** All tables have indexes on `id` (UUID)
2. **Foreign Keys:** Indexes on `host_id`, `user_id`, `location_id`
3. **Unique Constraints:** Index on `users.email`

**Additional recommended indexes for production:**

```sql
-- For geospatial queries
CREATE INDEX idx_locations_coordinates ON locations(lat, lng);

-- For city-based searches
CREATE INDEX idx_locations_city ON locations(city) WHERE is_active = true;

-- For time-based booking queries
CREATE INDEX idx_bookings_time_range ON bookings(start_time, end_time);

-- For status filtering
CREATE INDEX idx_bookings_status ON bookings(status);

-- For role-based user queries
CREATE INDEX idx_users_role ON users(role);
```

### Query Optimization Tips

1. **Use LAZY fetching:** All relationships are LAZY loaded to avoid N+1 queries
2. **Project to DTOs:** Don't expose full entity graphs; use DTOs
3. **Batch queries:** Use JPA batch fetching for related entities
4. **Limit results:** Always use `LIMIT` for pagination
5. **Use covering indexes:** Ensure indexes cover common query patterns

### Connection Pooling

Default HikariCP settings (configured in Spring Boot):
- Maximum pool size: 10 connections
- Minimum idle: 10 connections
- Connection timeout: 30 seconds

## Schema Migration

### Current Strategy

**Development:** `spring.jpa.hibernate.ddl-auto=update`
- JPA automatically updates schema based on entity changes
- Not recommended for production

### Production Recommendations

**Option 1: Flyway**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

**Option 2: Liquibase**
```xml
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```

### Migration Best Practices

1. **Never use `ddl-auto=update` in production**
2. **Version control all schema changes**
3. **Test migrations on staging environment first**
4. **Create rollback scripts for each migration**
5. **Back up database before applying migrations**

## Security Considerations

### Password Storage

- Passwords are hashed using BCrypt (via Spring Security's `PasswordEncoder`)
- Hash prefix: `$2a$10$` (BCrypt with cost factor 10)
- Never store plain text passwords

### SQL Injection Prevention

- All queries use parameterized statements (JPA/JDBC)
- Native queries use positional parameters (`?1`, `?2`, etc.)
- Never concatenate user input into SQL strings

### Access Control

- Database user `luggo` should have limited permissions in production
- Create separate users for application and admin tasks
- Use SSL/TLS for database connections in production

### Data Privacy

- `password_hash` should never be included in API responses
- Implement audit logging for sensitive operations
- Consider encrypting sensitive fields (e.g., email) at rest

## Monitoring and Maintenance

### Useful Queries

**Check table sizes:**
```sql
SELECT
  schemaname,
  tablename,
  pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

**Check active connections:**
```sql
SELECT count(*) FROM pg_stat_activity WHERE datname = 'luggage-backend';
```

**Find slow queries:**
```sql
SELECT
  query,
  mean_exec_time,
  calls
FROM pg_stat_statements
ORDER BY mean_exec_time DESC
LIMIT 10;
```

## Troubleshooting

### Common Issues

**Issue: Port 5432 already in use**
```bash
# Stop local PostgreSQL
brew services stop postgresql@14
```

**Issue: Connection refused**
```bash
# Check if Docker container is running
docker ps | grep postgres

# Restart container
docker-compose restart
```

**Issue: Duplicate key error on mock data load**
```bash
# Clear existing data
PGPASSWORD=luggo psql -h localhost -U luggo -d luggage-backend -c \
  "TRUNCATE TABLE bookings, locations, users CASCADE;"

# Set spring.sql.init.mode=never after first run
```

**Issue: Schema out of sync**
```bash
# Drop and recreate (development only!)
docker-compose down -v
docker-compose up -d
# Then restart application
```

## Future Enhancements

Potential database improvements:

1. **Partitioning:** Partition `bookings` table by date for better performance
2. **Full-text search:** Add PostgreSQL full-text search on location names/addresses
3. **Materialized views:** Cache expensive aggregation queries
4. **Point/Geography types:** Use PostGIS for better geospatial support
5. **Audit tables:** Track all changes to sensitive data
6. **Read replicas:** Add read-only replicas for scaling
7. **Time-series data:** Store metrics in separate time-series database

## Resources

- [PostgreSQL Documentation](https://www.postgresql.org/docs/14/)
- [JPA/Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Haversine Formula](https://en.wikipedia.org/wiki/Haversine_formula)