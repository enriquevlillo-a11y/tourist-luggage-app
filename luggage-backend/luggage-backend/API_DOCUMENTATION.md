# Luggage Backend API Documentation

Base URL: `http://localhost:8081`

This document provides detailed information about each API endpoint, including what data is needed and how to test in Postman.

---

## Table of Contents
- [Users Module](#users-module)
- [Locations Module](#locations-module)
- [Host Module](#host-module)
- [Bookings Module](#bookings-module)
- [Testing in Postman](#testing-in-postman)

---

## Users Module

### 1. Register User
**POST** `/api/users/register`

**Description:** Create a new user account. Role defaults to USER if not specified.

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "newuser@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "role": "USER"
}
```

**Field Descriptions:**
- `email` (required): Valid email address
- `password` (required): Minimum 6 characters
- `fullName` (required): 2-100 characters
- `role` (optional): USER, HOST, or ADMIN (defaults to USER)

**Response (201 Created):**
```json
{
  "userId": "uuid",
  "email": "newuser@example.com",
  "fullName": "John Doe",
  "role": "USER",
  "message": "User registered successfully"
}
```

**Postman Setup:**
1. Method: POST
2. URL: `http://localhost:8081/api/users/register`
3. Headers: Add `Content-Type: application/json`
4. Body: Select "raw" and "JSON", paste the request body
5. Click Send

---

### 2. Login
**POST** `/api/users/login`

**Description:** Authenticate user with email and password. Returns user information.

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "john.doe@email.com",
  "password": "password123"
}
```

**Field Descriptions:**
- `email` (required): Registered email address
- `password` (required): User's password

**Response (200 OK):**
```json
{
  "userId": "11111111-1111-1111-1111-111111111111",
  "email": "john.doe@email.com",
  "fullName": "John Doe",
  "role": "USER",
  "message": "Login successful"
}
```

**Postman Setup:**
1. Method: POST
2. URL: `http://localhost:8081/api/users/login`
3. Headers: Add `Content-Type: application/json`
4. Body: Select "raw" and "JSON", paste request body with valid credentials
5. Click Send
6. **Save the userId from response** - you'll need it for authenticated requests

---

### 3. Get Current User Profile
**GET** `/api/users/me`

**Description:** Get profile information for the currently authenticated user.

**Headers:**
```
X-User-Id: 11111111-1111-1111-1111-111111111111
```

**Request Body:** None

**Response (200 OK):**
```json
{
  "id": "11111111-1111-1111-1111-111111111111",
  "email": "john.doe@email.com",
  "fullName": "John Doe",
  "role": "USER",
  "createdAt": "2025-01-20T10:00:00Z",
  "updatedAt": "2025-01-20T10:00:00Z",
  "totalBookings": 2,
  "totalLocations": 0
}
```

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/users/me`
3. Headers: Add `X-User-Id` with a valid user UUID
4. Click Send

---

### 4. Get User By ID
**GET** `/api/users/{userId}`

**Description:** Get a specific user's profile by their UUID.

**Headers:** None required

**URL Parameters:**
- `userId`: The UUID of the user to retrieve

**Example URL:** `http://localhost:8081/api/users/11111111-1111-1111-1111-111111111111`

**Response (200 OK):** Same as "Get Current User Profile"

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/users/11111111-1111-1111-1111-111111111111`
3. Click Send

---

### 5. Get All Users
**GET** `/api/users`

**Description:** Retrieve all registered users (should be admin-only in production).

**Headers:** None required

**Request Body:** None

**Response (200 OK):**
```json
[
  {
    "id": "11111111-1111-1111-1111-111111111111",
    "email": "john.doe@email.com",
    "fullName": "John Doe",
    "role": "USER",
    "createdAt": "2025-01-20T10:00:00Z",
    "updatedAt": "2025-01-20T10:00:00Z",
    "totalBookings": 2,
    "totalLocations": 0
  },
  ...
]
```

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/users`
3. Click Send

---

### 6. Get Users By Role
**GET** `/api/users/role/{role}`

**Description:** Get all users with a specific role.

**URL Parameters:**
- `role`: USER, HOST, or ADMIN

**Example URL:** `http://localhost:8081/api/users/role/HOST`

**Response (200 OK):** Array of users with the specified role

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/users/role/HOST`
3. Click Send

---

### 7. Update User Profile
**PUT** `/api/users/{userId}`

**Description:** Update user profile. Users can only update their own profile.

**Headers:**
```
Content-Type: application/json
X-User-Id: 11111111-1111-1111-1111-111111111111
```

**URL Parameters:**
- `userId`: The UUID of the user to update (must match X-User-Id header)

**Request Body:**
```json
{
  "email": "newemail@example.com",
  "fullName": "John Updated Doe"
}
```

**Field Descriptions:**
- `email` (optional): New email address (must be valid and not already in use)
- `fullName` (optional): New full name (2-100 characters)

**Response (200 OK):** Updated user profile

**Postman Setup:**
1. Method: PUT
2. URL: `http://localhost:8081/api/users/11111111-1111-1111-1111-111111111111`
3. Headers: Add `Content-Type: application/json` and `X-User-Id: 11111111-1111-1111-1111-111111111111`
4. Body: Select "raw" and "JSON", paste request body
5. Click Send

---

### 8. Change Password
**PUT** `/api/users/{userId}/password`

**Description:** Change user password. Requires current password verification.

**Headers:**
```
Content-Type: application/json
X-User-Id: 11111111-1111-1111-1111-111111111111
```

**URL Parameters:**
- `userId`: The UUID of the user (must match X-User-Id header)

**Request Body:**
```json
{
  "currentPassword": "password123",
  "newPassword": "newpassword456",
  "confirmPassword": "newpassword456"
}
```

**Field Descriptions:**
- `currentPassword` (required): User's current password
- `newPassword` (required): New password (minimum 6 characters)
- `confirmPassword` (required): Must match newPassword

**Response (200 OK):**
```json
{
  "message": "Password changed successfully"
}
```

**Postman Setup:**
1. Method: PUT
2. URL: `http://localhost:8081/api/users/11111111-1111-1111-1111-111111111111/password`
3. Headers: Add `Content-Type: application/json` and `X-User-Id`
4. Body: Select "raw" and "JSON", paste request body
5. Click Send

---

### 9. Upgrade User to Host
**PATCH** `/api/users/{userId}/upgrade-to-host`

**Description:** Upgrade a USER account to HOST role. Users can only upgrade their own account.

**Headers:**
```
X-User-Id: 11111111-1111-1111-1111-111111111111
```

**URL Parameters:**
- `userId`: The UUID of the user to upgrade (must match X-User-Id header)

**Request Body:** None

**Response (200 OK):** Updated user profile with HOST role

**Postman Setup:**
1. Method: PATCH
2. URL: `http://localhost:8081/api/users/11111111-1111-1111-1111-111111111111/upgrade-to-host`
3. Headers: Add `X-User-Id: 11111111-1111-1111-1111-111111111111`
4. Click Send

---

### 10. Delete User
**DELETE** `/api/users/{userId}`

**Description:** Delete user account. Users can only delete their own account.

**Headers:**
```
X-User-Id: 11111111-1111-1111-1111-111111111111
```

**URL Parameters:**
- `userId`: The UUID of the user to delete (must match X-User-Id header)

**Request Body:** None

**Response (204 No Content):** Empty response on success

**Postman Setup:**
1. Method: DELETE
2. URL: `http://localhost:8081/api/users/11111111-1111-1111-1111-111111111111`
3. Headers: Add `X-User-Id: 11111111-1111-1111-1111-111111111111`
4. Click Send

---

### 11. Search Users
**GET** `/api/users/search?q={query}`

**Description:** Search users by name or email (should be admin-only in production).

**Query Parameters:**
- `q`: Search query string

**Example URL:** `http://localhost:8081/api/users/search?q=john`

**Response (200 OK):** Array of matching users

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/users/search`
3. Params: Add key `q` with value `john`
4. Click Send

---

### 12. Check Email Exists
**GET** `/api/users/check-email?email={email}`

**Description:** Check if an email is already registered. Useful for frontend validation.

**Query Parameters:**
- `email`: Email address to check

**Example URL:** `http://localhost:8081/api/users/check-email?email=john.doe@email.com`

**Response (200 OK):**
```json
{
  "exists": true
}
```

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/users/check-email`
3. Params: Add key `email` with value `john.doe@email.com`
4. Click Send

---

### 13. Get User By Email
**GET** `/api/users/by-email?email={email}`

**Description:** Get user profile by email address (should be admin-only in production).

**Query Parameters:**
- `email`: User's email address

**Example URL:** `http://localhost:8081/api/users/by-email?email=john.doe@email.com`

**Response (200 OK):** User profile object

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/users/by-email`
3. Params: Add key `email` with value `john.doe@email.com`
4. Click Send

---

## Locations Module

### 1. Find Nearby Locations
**POST** `/api/locations/nearby`

**Description:** Find storage locations near a specific coordinate using the Haversine formula. Results are ordered by distance (closest first).

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "latitude": 40.7580,
  "longitude": -73.9855,
  "radiusKm": 10.0
}
```

**Field Descriptions:**
- `latitude` (required): User's latitude coordinate
- `longitude` (required): User's longitude coordinate
- `radiusKm` (optional): Search radius in kilometers (defaults to 5.0)

**Response (200 OK):**
```json
[
  {
    "id": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
    "name": "Times Square Luggage Hub",
    "address": "1560 Broadway",
    "city": "New York",
    "lat": 40.7580,
    "lng": -73.9855,
    "pricePerHour": 5.00,
    "capacity": 50,
    "hours": "24/7",
    "isActive": true,
    "totalBookings": 2,
    "host": {
      "id": "55555555-5555-5555-5555-555555555555",
      "fullName": "Sarah Johnson",
      "email": "sarah.host@email.com"
    }
  },
  ...
]
```

**Postman Setup:**
1. Method: POST
2. URL: `http://localhost:8081/api/locations/nearby`
3. Headers: Add `Content-Type: application/json`
4. Body: Select "raw" and "JSON", paste request body
5. **Tip:** Use coordinates for New York (40.7580, -73.9855), Paris (48.8566, 2.3522), Tokyo (35.6762, 139.6503), or London (51.5074, -0.1278) to find mock data locations
6. Click Send

---

### 2. Find Nearby With Filters
**POST** `/api/locations/nearby/filtered`

**Description:** Find nearby locations with optional price and capacity filters.

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "latitude": 40.7580,
  "longitude": -73.9855,
  "radiusKm": 10.0,
  "minPrice": 3.0,
  "maxPrice": 10.0,
  "minCapacity": 20
}
```

**Field Descriptions:**
- `latitude` (required): User's latitude
- `longitude` (required): User's longitude
- `radiusKm` (optional): Search radius in km (default 5.0)
- `minPrice` (optional): Minimum price per hour
- `maxPrice` (optional): Maximum price per hour
- `minCapacity` (optional): Minimum capacity required

**Response (200 OK):** Array of filtered locations

**Postman Setup:**
1. Method: POST
2. URL: `http://localhost:8081/api/locations/nearby/filtered`
3. Headers: Add `Content-Type: application/json`
4. Body: Select "raw" and "JSON", paste request body
5. Click Send

---

### 3. Get All Locations
**GET** `/api/locations`

**Description:** Retrieve all available storage locations.

**Headers:** None required

**Request Body:** None

**Response (200 OK):** Array of all locations

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/locations`
3. Click Send

---

### 4. Get Location By ID
**GET** `/api/locations/{id}`

**Description:** Get detailed information about a specific location.

**URL Parameters:**
- `id`: Location UUID

**Example URL:** `http://localhost:8081/api/locations/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa`

**Response (200 OK):** Location object with full details

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/locations/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa`
3. **Mock Data IDs you can use:**
   - `aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa` - Times Square Luggage Hub (New York)
   - `bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb` - Central Park Storage (New York)
   - `cccccccc-cccc-cccc-cccc-cccccccccccc` - Eiffel Tower Bags (Paris)
   - `dddddddd-dddd-dddd-dddd-dddddddddddd` - Louvre Luggage Depot (Paris)
4. Click Send

---

### 5. Create Location
**POST** `/api/locations`

**Description:** Create a new storage location. Only users with HOST role can create locations.

**Headers:**
```
Content-Type: application/json
X-User-Id: 55555555-5555-5555-5555-555555555555
```

**Request Body:**
```json
{
  "name": "Downtown Luggage Storage",
  "address": "123 Main Street",
  "city": "New York",
  "latitude": 40.7489,
  "longitude": -73.9680,
  "pricePerHour": 6.50,
  "capacity": 30,
  "hours": "Mon-Sun: 8AM-10PM"
}
```

**Field Descriptions:**
- `name` (required): Location name
- `address` (required): Street address
- `city` (required): City name
- `latitude` (required): Latitude coordinate
- `longitude` (required): Longitude coordinate
- `pricePerHour` (required): Price per hour as decimal
- `capacity` (required): Maximum number of items
- `hours` (required): Operating hours description

**Response (201 Created):** Created location object

**Postman Setup:**
1. Method: POST
2. URL: `http://localhost:8081/api/locations`
3. Headers: Add `Content-Type: application/json` and `X-User-Id` (use a HOST user ID)
4. **Mock Host IDs you can use:**
   - `55555555-5555-5555-5555-555555555555` - Sarah Johnson
   - `66666666-6666-6666-6666-666666666666` - Mike Chen
   - `77777777-7777-7777-7777-777777777777` - Emma Wilson
   - `88888888-8888-8888-8888-888888888888` - David Brown
5. Body: Select "raw" and "JSON", paste request body
6. Click Send

---

### 6. Update Location
**PUT** `/api/locations/{id}`

**Description:** Update an existing location. Only the host who owns the location can update it.

**Headers:**
```
Content-Type: application/json
X-User-Id: 55555555-5555-5555-5555-555555555555
```

**URL Parameters:**
- `id`: Location UUID to update

**Request Body:**
```json
{
  "name": "Updated Location Name",
  "address": "456 New Address",
  "city": "New York",
  "latitude": 40.7580,
  "longitude": -73.9855,
  "pricePerHour": 7.00,
  "capacity": 60,
  "hours": "24/7"
}
```

**Response (200 OK):** Updated location object

**Postman Setup:**
1. Method: PUT
2. URL: `http://localhost:8081/api/locations/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa`
3. Headers: Add `Content-Type: application/json` and `X-User-Id` (must be the owner)
4. Body: Select "raw" and "JSON", paste request body
5. **Important:** X-User-Id must match the location's host, or you'll get 403 Forbidden
6. Click Send

---

### 7. Delete Location
**DELETE** `/api/locations/{id}`

**Description:** Delete a location. Only the host who owns the location can delete it.

**Headers:**
```
X-User-Id: 55555555-5555-5555-5555-555555555555
```

**URL Parameters:**
- `id`: Location UUID to delete

**Request Body:** None

**Response (204 No Content):** Empty response on success

**Postman Setup:**
1. Method: DELETE
2. URL: `http://localhost:8081/api/locations/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa`
3. Headers: Add `X-User-Id` (must be the location owner)
4. Click Send

---

### 8. Get Locations By Host
**GET** `/api/locations/host/{hostId}`

**Description:** Get all locations owned by a specific host.

**URL Parameters:**
- `hostId`: Host user UUID

**Example URL:** `http://localhost:8081/api/locations/host/55555555-5555-5555-5555-555555555555`

**Response (200 OK):** Array of locations owned by the host

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/locations/host/55555555-5555-5555-5555-555555555555`
3. **Mock Host IDs:** See Create Location section for valid host IDs
4. Click Send

---

### 9. Search Locations
**GET** `/api/locations/search?q={query}`

**Description:** Search locations by keyword (case-insensitive). Searches in name and address fields.

**Query Parameters:**
- `q`: Search query string

**Example URL:** `http://localhost:8081/api/locations/search?q=times square`

**Response (200 OK):** Array of matching locations

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/locations/search`
3. Params: Add key `q` with value like `times square`, `paris`, `tokyo`, etc.
4. Click Send

---

### 10. Filter Locations - By Price Range
**GET** `/api/locations/filter?minPrice={min}&maxPrice={max}`

**Description:** Filter locations by price range per hour.

**Query Parameters:**
- `minPrice`: Minimum price per hour
- `maxPrice`: Maximum price per hour

**Example URL:** `http://localhost:8081/api/locations/filter?minPrice=5&maxPrice=15`

**Response (200 OK):** Array of locations within price range

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/locations/filter`
3. Params: Add `minPrice` = `5` and `maxPrice` = `15`
4. Click Send

---

### 11. Filter Locations - By Capacity
**GET** `/api/locations/filter?minCapacity={capacity}`

**Description:** Filter locations by minimum capacity.

**Query Parameters:**
- `minCapacity`: Minimum capacity required

**Example URL:** `http://localhost:8081/api/locations/filter?minCapacity=30`

**Response (200 OK):** Array of locations with sufficient capacity

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/locations/filter`
3. Params: Add `minCapacity` = `30`
4. Click Send

---

### 12. Filter Locations - By City
**GET** `/api/locations/filter?city={cityName}`

**Description:** Filter locations by city name.

**Query Parameters:**
- `city`: City name

**Example URL:** `http://localhost:8081/api/locations/filter?city=Paris`

**Response (200 OK):** Array of locations in the specified city

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/locations/filter`
3. Params: Add `city` = `Paris` (or `New York`, `Tokyo`, `London`)
4. Click Send

---

### 13. Get All Cities
**GET** `/api/locations/cities`

**Description:** Get a list of all unique cities that have storage locations.

**Headers:** None required

**Request Body:** None

**Response (200 OK):**
```json
["New York", "Paris", "Tokyo", "London"]
```

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/locations/cities`
3. Click Send

---

### 14. Get Locations By City
**GET** `/api/locations/city/{city}`

**Description:** Get all active locations in a specific city.

**URL Parameters:**
- `city`: City name

**Example URL:** `http://localhost:8081/api/locations/city/Tokyo`

**Response (200 OK):** Array of locations in the city

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/locations/city/Tokyo`
3. Click Send

---

### 15. Get Popular Locations
**GET** `/api/locations/popular?limit={number}`

**Description:** Get most popular locations based on booking count.

**Query Parameters:**
- `limit` (optional): Maximum number of locations to return (default: 10)

**Example URL:** `http://localhost:8081/api/locations/popular?limit=5`

**Response (200 OK):** Array of popular locations sorted by booking count

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/locations/popular`
3. Params: Add `limit` = `5` (optional)
4. Click Send

---

### 16. Check Location Availability
**GET** `/api/locations/{id}/availability?startTime={start}&endTime={end}&capacity={num}`

**Description:** Check if a location has capacity available for a specific time period.

**URL Parameters:**
- `id`: Location UUID

**Query Parameters:**
- `startTime`: Booking start time (ISO 8601 format)
- `endTime`: Booking end time (ISO 8601 format)
- `capacity`: Required capacity

**Example URL:** `http://localhost:8081/api/locations/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/availability?startTime=2025-01-25T10:00:00Z&endTime=2025-01-25T14:00:00Z&capacity=5`

**Response (200 OK):**
```json
{
  "available": true
}
```

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/locations/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/availability`
3. Params:
   - `startTime` = `2025-01-25T10:00:00Z`
   - `endTime` = `2025-01-25T14:00:00Z`
   - `capacity` = `5`
4. **Date Format:** Use ISO 8601 format (YYYY-MM-DDTHH:mm:ssZ)
5. Click Send

---

### 17. Toggle Location Status
**PATCH** `/api/locations/{id}/status`

**Description:** Toggle location active/inactive status. Only the owner can change status.

**Headers:**
```
Content-Type: application/json
X-User-Id: 55555555-5555-5555-5555-555555555555
```

**URL Parameters:**
- `id`: Location UUID

**Request Body:**
```json
{
  "isActive": false
}
```

**Field Descriptions:**
- `isActive` (required): Boolean - true to activate, false to deactivate

**Response (200 OK):** Updated location object

**Postman Setup:**
1. Method: PATCH
2. URL: `http://localhost:8081/api/locations/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/status`
3. Headers: Add `Content-Type: application/json` and `X-User-Id` (must be owner)
4. Body: Select "raw" and "JSON", paste request body
5. Click Send

---

## Host Module

### 1. Get All Bookings (Host)
**GET** `/api/host/bookings`

**Description:** Get all bookings across all locations owned by the host.

**Headers:**
```
X-User-Id: 55555555-5555-5555-5555-555555555555
```

**Request Body:** None

**Response (200 OK):**
```json
[
  {
    "id": "f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0",
    "startTime": "2025-01-20T10:00:00Z",
    "endTime": "2025-01-20T16:00:00Z",
    "priceCents": 3000,
    "status": "CONFIRMED",
    "user": {
      "id": "11111111-1111-1111-1111-111111111111",
      "fullName": "John Doe",
      "email": "john.doe@email.com"
    },
    "location": {
      "id": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
      "name": "Times Square Luggage Hub",
      "address": "1560 Broadway",
      "city": "New York"
    }
  },
  ...
]
```

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/host/bookings`
3. Headers: Add `X-User-Id` with a HOST user ID
4. **Mock Host IDs:** 55555555-5555-5555-5555-555555555555 (Sarah Johnson has 2 bookings)
5. Click Send

---

### 2. Get Bookings For Location
**GET** `/api/host/locations/{locationId}/bookings`

**Description:** Get all bookings for a specific location. Verifies the location belongs to the requesting host.

**Headers:**
```
X-User-Id: 55555555-5555-5555-5555-555555555555
```

**URL Parameters:**
- `locationId`: Location UUID

**Example URL:** `http://localhost:8081/api/host/locations/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/bookings`

**Response (200 OK):** Array of bookings for the location

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/host/locations/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa/bookings`
3. Headers: Add `X-User-Id` (must own the location)
4. **Important:** If X-User-Id doesn't own the location, returns 403 Forbidden
5. Click Send

---

### 3. Get Dashboard Statistics
**GET** `/api/host/dashboard`

**Description:** Get booking statistics dashboard for the host (total bookings, pending, confirmed, cancelled, completed).

**Headers:**
```
X-User-Id: 55555555-5555-5555-5555-555555555555
```

**Request Body:** None

**Response (200 OK):**
```json
{
  "totalBookings": 2,
  "pendingBookings": 0,
  "confirmedBookings": 2,
  "cancelledBookings": 0,
  "completedBookings": 0
}
```

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/host/dashboard`
3. Headers: Add `X-User-Id` with a HOST user ID
4. Click Send

---

## Bookings Module

### 1. Create Booking
**POST** `/api/bookings`

**Description:** Create a new booking at a storage location. Price is automatically calculated based on duration and location's hourly rate.

**Headers:**
```
Content-Type: application/json
X-User-Id: 11111111-1111-1111-1111-111111111111
```

**Request Body:**
```json
{
  "locationId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
  "startTime": "2025-12-01T10:00:00Z",
  "endTime": "2025-12-01T16:00:00Z",
  "numberOfItems": 2
}
```

**Field Descriptions:**
- `locationId` (required): UUID of the storage location
- `startTime` (required): Booking start time (ISO 8601 format, must be in future)
- `endTime` (required): Booking end time (must be after startTime)
- `numberOfItems` (optional): Number of items/bags to store

**Response (201 Created):**
```json
{
  "id": "4e777fa6-691f-480d-9442-fcbf96956e0d",
  "user": {
    "id": "11111111-1111-1111-1111-111111111111",
    "fullName": "John Doe",
    "email": "john.doe@email.com"
  },
  "location": {
    "id": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
    "name": "Times Square Luggage Hub",
    "address": "1560 Broadway"
  },
  "startTime": "2025-12-01T10:00:00Z",
  "endTime": "2025-12-01T16:00:00Z",
  "priceCents": 3000,
  "status": "PENDING"
}
```

**Postman Setup:**
1. Method: POST
2. URL: `http://localhost:8081/api/bookings`
3. Headers: Add `Content-Type: application/json` and `X-User-Id` (customer UUID)
4. Body: Select "raw" and "JSON", paste request body
5. **Important:** Use a future date for startTime
6. **Price Calculation:** For 6 hours at $5/hour = $30 = 3000 cents
7. Click Send

---

### 2. Get All Bookings
**GET** `/api/bookings`

**Description:** Get all bookings in the system (admin/testing only, should be restricted in production).

**Headers:** None required

**Request Body:** None

**Response (200 OK):**
```json
[
  {
    "id": "b1b1b1b1-b1b1-b1b1-b1b1-b1b1b1b1b1b1",
    "user": {
      "id": "11111111-1111-1111-1111-111111111111",
      "fullName": "John Doe",
      "email": "john.doe@email.com"
    },
    "location": {
      "id": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
      "name": "Times Square Luggage Hub",
      "address": "1560 Broadway"
    },
    "startTime": "2025-10-24T00:03:04.964471Z",
    "endTime": "2025-10-27T00:03:04.964471Z",
    "priceCents": 7200,
    "status": "CONFIRMED"
  },
  ...
]
```

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/bookings`
3. Click Send

---

### 3. Get Booking By ID
**GET** `/api/bookings/{bookingId}`

**Description:** Get details of a specific booking by its UUID.

**URL Parameters:**
- `bookingId`: Booking UUID

**Example URL:** `http://localhost:8081/api/bookings/b1b1b1b1-b1b1-b1b1-b1b1-b1b1b1b1b1b1`

**Response (200 OK):** Single booking object

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/bookings/b1b1b1b1-b1b1-b1b1-b1b1-b1b1b1b1b1b1`
3. **Mock Booking IDs you can use:**
   - `b1b1b1b1-b1b1-b1b1-b1b1-b1b1b1b1b1b1` - John's confirmed booking
   - `b4b4b4b4-b4b4-b4b4-b4b4-b4b4b4b4b4b4` - Emma's pending booking
   - `b6b6b6b6-b6b6-b6b6-b6b6-b6b6b6b6b6b6` - Completed booking
   - `b8b8b8b8-b8b8-b8b8-b8b8-b8b8b8b8b8b8` - Cancelled booking
4. Click Send

---

### 4. Get My Bookings
**GET** `/api/bookings/me`

**Description:** Get all bookings for the currently authenticated user.

**Headers:**
```
X-User-Id: 11111111-1111-1111-1111-111111111111
```

**Request Body:** None

**Response (200 OK):** Array of user's bookings

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/bookings/me`
3. Headers: Add `X-User-Id` with user UUID
4. **Mock User IDs:** John Doe (11111111...) has 3 bookings
5. Click Send

---

### 5. Get User Bookings By ID
**GET** `/api/bookings/user/{userId}`

**Description:** Get all bookings for a specific user by their UUID.

**URL Parameters:**
- `userId`: User UUID

**Example URL:** `http://localhost:8081/api/bookings/user/11111111-1111-1111-1111-111111111111`

**Response (200 OK):** Array of user's bookings

**Postman Setup:**
1. Method: GET
2. URL: `http://localhost:8081/api/bookings/user/11111111-1111-1111-1111-111111111111`
3. Click Send

---

### 6. Update Booking
**PUT** `/api/bookings/{bookingId}`

**Description:** Update booking details (time, items). Only PENDING bookings can be updated. User can only update their own bookings. Price is automatically recalculated if times change.

**Headers:**
```
Content-Type: application/json
X-User-Id: 11111111-1111-1111-1111-111111111111
```

**URL Parameters:**
- `bookingId`: Booking UUID to update

**Request Body:**
```json
{
  "startTime": "2025-12-01T11:00:00Z",
  "endTime": "2025-12-01T17:00:00Z",
  "numberOfItems": 3
}
```

**Field Descriptions:**
- `startTime` (optional): New start time (must be in future)
- `endTime` (optional): New end time (must be after startTime)
- `numberOfItems` (optional): Updated number of items

**Response (200 OK):** Updated booking with recalculated price

**Postman Setup:**
1. Method: PUT
2. URL: `http://localhost:8081/api/bookings/{bookingId}`
3. Headers: Add `Content-Type: application/json` and `X-User-Id` (must be booking owner)
4. Body: Select "raw" and "JSON", paste request body (all fields optional)
5. **Important:** Only works on PENDING bookings
6. **Price Recalculation:** If you change from 6 hours to 8 hours, price updates automatically
7. Click Send

---

### 7. Cancel Booking
**DELETE** `/api/bookings/{bookingId}`

**Description:** Cancel a booking. User can only cancel their own PENDING or CONFIRMED bookings. Cannot cancel COMPLETED bookings.

**Headers:**
```
X-User-Id: 11111111-1111-1111-1111-111111111111
```

**URL Parameters:**
- `bookingId`: Booking UUID to cancel

**Request Body:** None

**Response (200 OK):**
```json
{
  "message": "Booking cancelled successfully"
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Cannot cancel a completed booking"
}
```

**Postman Setup:**
1. Method: DELETE
2. URL: `http://localhost:8081/api/bookings/{bookingId}`
3. Headers: Add `X-User-Id` (must be booking owner)
4. **Important:** User can only cancel their own bookings
5. Click Send

---

### 8. Confirm Booking (Host Only)
**PATCH** `/api/bookings/{bookingId}/confirm`

**Description:** Confirm a booking (host only). Changes status from PENDING to CONFIRMED. Only the location owner can confirm bookings.

**Headers:**
```
X-User-Id: 55555555-5555-5555-5555-555555555555
```

**URL Parameters:**
- `bookingId`: Booking UUID to confirm

**Request Body:** None

**Response (200 OK):** Updated booking with CONFIRMED status

**Postman Setup:**
1. Method: PATCH
2. URL: `http://localhost:8081/api/bookings/{bookingId}/confirm`
3. Headers: Add `X-User-Id` with HOST user ID (must be location owner)
4. **Mock Host IDs:**
   - Sarah Johnson (55555555...) owns Times Square & Central Park
   - Mike Chen (66666666...) owns Eiffel Tower & Louvre
   - Emma Wilson (77777777...) owns Shibuya & Shinjuku
   - David Brown (88888888...) owns Kings Cross & Covent Garden
5. **Important:** Returns 403 Forbidden if X-User-Id is not the location owner
6. Click Send

---

### 9. Complete Booking (Host Only)
**PATCH** `/api/bookings/{bookingId}/complete`

**Description:** Mark a booking as complete (host only). Changes status from CONFIRMED to COMPLETED. Usually done after the booking end time has passed.

**Headers:**
```
X-User-Id: 55555555-5555-5555-5555-555555555555
```

**URL Parameters:**
- `bookingId`: Booking UUID to complete

**Request Body:** None

**Response (200 OK):** Updated booking with COMPLETED status

**Postman Setup:**
1. Method: PATCH
2. URL: `http://localhost:8081/api/bookings/{bookingId}/complete`
3. Headers: Add `X-User-Id` with HOST user ID (must be location owner)
4. **Important:** Only CONFIRMED bookings can be completed
5. Click Send

---

### Booking Lifecycle

```
Customer creates booking → PENDING
         ↓
Host confirms booking → CONFIRMED
         ↓
Service ends, host marks complete → COMPLETED

Alternative paths:
PENDING → CANCELLED (customer cancels)
CONFIRMED → CANCELLED (customer cancels)
```

### Booking Status Descriptions

- **PENDING** - Booking created, waiting for host confirmation
- **CONFIRMED** - Host has confirmed the booking
- **COMPLETED** - Service has been provided and booking is finished
- **CANCELLED** - Booking was cancelled (by customer)

---

## Testing in Postman

### Setting Up Your Environment

1. **Create an Environment Variable:**
   - Click "Environments" in Postman
   - Create new environment called "Luggage Backend Local"
   - Add variable: `baseUrl` = `http://localhost:8081`
   - Save and select this environment

2. **Using Variables in Requests:**
   - Replace `http://localhost:8081` with `{{baseUrl}}` in all URLs
   - Example: `{{baseUrl}}/api/users/login`

### Quick Start Testing Workflow

**Step 1: Login to get a user ID**
```
POST {{baseUrl}}/api/users/login
Body: {"email": "john.doe@email.com", "password": "password123"}
```
Copy the `userId` from the response.

**Step 2: Test authenticated endpoints**
Use the copied `userId` in the `X-User-Id` header for protected endpoints.

**Step 3: Test location search**
```
POST {{baseUrl}}/api/locations/nearby
Body: {"latitude": 40.7580, "longitude": -73.9855, "radiusKm": 10.0}
```

**Step 4: Test host functionality**
```
GET {{baseUrl}}/api/host/dashboard
Header: X-User-Id: 55555555-5555-5555-5555-555555555555
```

### Mock Data Reference

**Users:**
- `11111111-1111-1111-1111-111111111111` - John Doe (USER)
- `22222222-2222-2222-2222-222222222222` - Jane Smith (USER)
- `33333333-3333-3333-3333-333333333333` - Bob Wilson (USER)
- `44444444-4444-4444-4444-444444444444` - Alice Brown (USER)
- `55555555-5555-5555-5555-555555555555` - Sarah Johnson (HOST)
- `66666666-6666-6666-6666-666666666666` - Mike Chen (HOST)
- `77777777-7777-7777-7777-777777777777` - Emma Wilson (HOST)
- `88888888-8888-8888-8888-888888888888` - David Brown (HOST)
- `99999999-9999-9999-9999-999999999999` - Admin User (ADMIN)

**Locations:**
- `aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa` - Times Square Luggage Hub (New York, Sarah Johnson)
- `bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb` - Central Park Storage (New York, Sarah Johnson)
- `cccccccc-cccc-cccc-cccc-cccccccccccc` - Eiffel Tower Bags (Paris, Mike Chen)
- `dddddddd-dddd-dddd-dddd-dddddddddddd` - Louvre Luggage Depot (Paris, Mike Chen)
- `eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee` - Shibuya Bag Keep (Tokyo, Emma Wilson)
- `ffffffff-ffff-ffff-ffff-ffffffffffff` - Shinjuku Storage (Tokyo, Emma Wilson)
- `10101010-1010-1010-1010-101010101010` - London Bridge Lockers (London, David Brown)
- `20202020-2020-2020-2020-202020202020` - Westminster Luggage (London, David Brown)

**All mock users have password:** `password123`

### Common HTTP Status Codes

- `200 OK` - Successful GET/PUT/PATCH request
- `201 Created` - Successful POST request (resource created)
- `204 No Content` - Successful DELETE request
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Invalid credentials (login failed)
- `403 Forbidden` - User doesn't have permission (wrong user ID)
- `404 Not Found` - Resource doesn't exist

### Tips for Testing

1. **Always check the response status code** to verify success
2. **Save important IDs** (userId, locationId) as environment variables for reuse
3. **Use the mock data IDs** provided above for consistent testing
4. **Test error cases:** Try accessing resources you don't own, use invalid IDs, etc.
5. **Test validation:** Try registering with a short password, invalid email, etc.
6. **Use collections:** Organize related requests into folders
7. **Write tests:** Use Postman's test scripts to automate validation

---

## Additional Notes

### Authentication (Development Mode)

Currently, the application uses the `X-User-Id` header for authentication. This is for **development and testing only**.

**In production, this should be replaced with:**
- JWT (JSON Web Tokens)
- Session-based authentication
- OAuth 2.0

### Password Security

Currently, passwords are stored in **plain text** (development only).

**In production, this must use:**
- BCrypt password hashing
- Password strength requirements
- Password reset functionality

### CORS

All controllers have `@CrossOrigin` annotation, allowing requests from any origin. In production, this should be restricted to your frontend domain.

---

**Last Updated:** January 2025
**API Version:** 1.0
**Base URL:** http://localhost:8081