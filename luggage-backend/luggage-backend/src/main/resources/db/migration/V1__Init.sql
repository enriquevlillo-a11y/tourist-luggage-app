CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE locations (
    id UUID PRIMARY KEY,
    host_id UUID NOT NULL REFERENCES users(id),
    name VARCHAR(255),
    address VARCHAR(255),
    city VARCHAR(255),
    lat DOUBLE PRECISION,
    lng DOUBLE PRECISION,
    price_per_hour DECIMAL(19, 2),
    capacity INTEGER,
    hours VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    location_id UUID REFERENCES locations(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    price_cents BIGINT,
    status VARCHAR(50) NOT NULL
);
